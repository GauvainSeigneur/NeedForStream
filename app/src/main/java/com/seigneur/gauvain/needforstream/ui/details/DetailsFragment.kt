package com.seigneur.gauvain.needforstream.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.ui.list.MainViewModel
import com.seigneur.gauvain.needforstream.utils.Constants
import timber.log.Timber
import android.provider.Contacts.People
import com.seigneur.gauvain.needforstream.utils.MathUtils
import kotlinx.android.synthetic.main.fragment_details.*
import org.json.JSONObject



class DetailsFragment: Fragment() {

    private val mMainViewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
    }


    private val mGson by lazy {
        Gson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.fragment_details, container,
            false
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        mMainViewModel?.mOpenedLiveCar?.observe(
            this,
            Observer { car ->
                Timber.d("car detail data: ${car.Brand}")
            }
        )

        mMainViewModel?.mFailureEvent?.observe(
            this,
            Observer { car ->
                Timber.d("error in frag")
            }
        )

        mMainViewModel?.mLiveCar?.observe(
            this,
            Observer {
                //Timber.d("lol $it")
                val car = mGson.fromJson(it, Car::class.java)
                val speed = MathUtils.getCurrentSpeedRange(car.CurrentSpeed, car.SpeedMax)
                Timber.d("current ratio: ${speed} speed ${car.CurrentSpeed}")
                progress.progress= speed
            }
        )
    }

    /*private fun subscribeToLiveDataT(){
        mMainViewModel?.mFailureEvent?.observe(
            activity,
            Observer {
                    t ->  Timber.d("error in fragmenr")
            }
        )

    }*/

}