package com.seigneur.gauvain.needforstream.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.data.rxWebSocket.RxWebSocket
import com.seigneur.gauvain.needforstream.data.rxWebSocket.RxWebSocketListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class MainViewModel
constructor() : ViewModel() {

    private val mCompositeDisposable = CompositeDisposable()

    private val httpLoggingInterceptor= HttpLoggingInterceptor()

    private var mWebSocket: WebSocket?=null

    private val mGson by lazy {
        Gson()
    }

    private val mClient by lazy {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    /**
     * Room (WebSocket) which we will access to observe data send and received from this room
     */
    private val mCarsRoom: Flowable<RxWebSocket> = Flowable.create({ e ->
        val listener = RxWebSocketListener(e)
        val request = Request.Builder().url("ws://pbbouachour.fr:8080/openSocket").build()
        mWebSocket = mClient.newWebSocket(request, listener)
        mClient
            .dispatcher()
            .executorService()
            .shutdown()
    }, BackpressureStrategy.BUFFER)

    private var isObservingCarsRoom =false

    private val mMutableCars= MutableLiveData<List<Car>>()

    /*
    *********************************************************************************************
    * LIFECYCLE
    *********************************************************************************************/
    public override fun onCleared() {
        super.onCleared()
        if (isObservingCarsRoom)
            mWebSocket?.close(NORMAL_CLOSURE_STATUS, "ViewModel being cleared")
        //clear disposable
        mCompositeDisposable.clear()
    }
    /*
    *********************************************************************************************
    * PUBLIC METHODS CALLED IN ACTIVITY
    *********************************************************************************************/
    fun init() {
        if  (!isObservingCarsRoom)
            observeCarsRoom()
        else
            Timber.d("already observe Cars Room")
    }

    val mLiveCars: LiveData<List<Car>>
        get() = mMutableCars

    /*
    *********************************************************************************************
    * PRIVATE METHODS
    *********************************************************************************************/
    private fun observeCarsRoom() {
        mCompositeDisposable.add(
            mCarsRoom
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isObservingCarsRoom=true
                }
                .subscribe(
                    { it ->
                        when (it) {
                            is RxWebSocket.Opened -> {
                                Timber.d("is open: ${it.response}")
                                sendCarsRequest(mWebSocket)
                            }
                            is RxWebSocket.StringMessage -> {
                                Timber.d("is received: ${it.text}")
                                val turnsType = object : TypeToken<List<Car>>() {}.type
                                val carList = mGson.fromJson<List<Car>>(it.text, turnsType)
                                mMutableCars.value = carList
                            }
                            is RxWebSocket.BinaryMessage -> {
                                Timber.d("is received: ${it.bytes}")
                            }
                            is RxWebSocket.Closing -> {
                                Timber.d("is Closing: ${it.code} Reason: ${it.reason}")
                            }
                            is RxWebSocket.Closed -> {
                                isObservingCarsRoom=false
                                Timber.d("is Closed: ${it.code} Reason: ${it.reason}")
                            }
                            is RxWebSocket.Failure -> {
                                Timber.d("is Failed: ${it.t} Response: ${it.response}")
                            }
                            else -> Timber.d("is else: $it")
                        }
                    },
                    { t ->
                        Timber.d("error on Cars room observer: $t")
                        isObservingCarsRoom=false
                    },
                    {
                        Timber.d("cars room observer complete")
                        isObservingCarsRoom=false
                    }
                )
        )
    }

    private fun sendCarsRequest(webSocket: WebSocket?) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Type", "infos")
            jsonObject.put("UserToken", 42)
            webSocket?.send(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val NORMAL_CLOSURE_STATUS = 1000
    }
}
