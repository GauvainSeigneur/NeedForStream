package com.seigneur.gauvain.needforstream.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.data.rxWebSocket.RxWebSocket
import com.seigneur.gauvain.needforstream.data.rxWebSocket.RxWebSocketListener
import com.seigneur.gauvain.needforstream.utils.Constants.LIST_REQUEST
import com.seigneur.gauvain.needforstream.utils.Constants.NORMAL_CLOSURE_STATUS
import com.seigneur.gauvain.needforstream.utils.Constants.SPEED_REQUEST
import com.seigneur.gauvain.needforstream.utils.Constants.STOP_REQUEST
import com.seigneur.gauvain.needforstream.utils.SingleLiveEvent
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

    private var mReceivedListValue = MutableLiveData<String>()
    private var mReceivedCarValue = MutableLiveData<String>()
    var mFailureEvent=SingleLiveEvent<Throwable>()

    var mOpenCarDetailView = SingleLiveEvent<Void>()
    private val mOpenedCar =MutableLiveData<Car>()
    var mCurrentRequest=-1

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
            //.shutdown()
    }, BackpressureStrategy.BUFFER)

    private var isObservingCarsRoom =false


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

    val mListLiveMessage: LiveData<String>
        get() = mReceivedListValue

    val mLiveCar: LiveData<String>
        get() = mReceivedCarValue

    fun startCar(car:Car?){
        Timber.d("clicked : ${car?.Brand}")
        mOpenCarDetailView.call()
        mOpenedCar.value =car
        val selectedCarRequest = "{\"Type\": \"start\", \"UserToken\": 42, \"Payload\": {\"Name\": \"${car?.Name}\"}}"
        mWebSocket?.send(selectedCarRequest)
        mCurrentRequest= SPEED_REQUEST
    }


    fun stopCar(){
        val stopRequest = "{\"Type\": \"stop\", \"UserToken\": 42}"
        mWebSocket?.send(stopRequest)
        mCurrentRequest= STOP_REQUEST
    }

    val mOpenedLiveCar: LiveData<Car>
        get() = mOpenedCar

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
                                when (mCurrentRequest){
                                    LIST_REQUEST -> {
                                        Timber.d("is list ${it.text}")
                                        mReceivedListValue.value = it.text
                                    }
                                    SPEED_REQUEST -> {
                                        Timber.d("is speed ${it.text}")
                                        mReceivedCarValue.value = it.text
                                    }
                                }
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
                                isObservingCarsRoom=false
                                Timber.d("Failure: ${it.t}")
                                sendFailure(it.t)
                            }
                            else -> Timber.d("is else: $it")
                        }
                    },
                    { t ->
                        Timber.d("error on Cars room observer: $t")
                        isObservingCarsRoom=false
                        sendFailure(t)
                    },
                    {
                        Timber.d("cars room observer complete")
                        isObservingCarsRoom=false
                    }
                )
        )
    }


    private fun sendCarsRequest(webSocket: WebSocket?) {
        mCurrentRequest= LIST_REQUEST

        val jsonObject = JSONObject()
        try {
            jsonObject.put("Type", "infos")
            jsonObject.put("UserToken", 42)
            webSocket?.send(jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun sendFailure(throwable: Throwable?) {
        mFailureEvent.value = throwable
    }
}
