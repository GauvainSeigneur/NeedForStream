package com.seigneur.gauvain.needforstream.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import com.seigneur.gauvain.needforstream.ui.list.MainViewModel
import timber.log.Timber
import com.seigneur.gauvain.needforstream.utils.MathUtils
import kotlinx.android.synthetic.main.fragment_details.*

/**
 * Detail Fragment for a selected car
 */
class DetailsFragment: Fragment() {

    private val mMainViewModel by lazy {
        activity?.let {
            //get the current instance of the view model and get the opened websocket
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
    }

    private val mGson by lazy {
        Gson()
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
                mCarBrand.text = car.Brand
                mCarName.text = car.Name
                mCarCV.text = "${car.Cv} CV"
            }
        )

        mMainViewModel?.mFailureEvent?.observe(
            this,
            Observer { t ->
                Timber.d("error in frag: $t")
            }
        )

        mMainViewModel?.mLiveCar?.observe(
            this,
            Observer {
                val car = mGson.fromJson(it, Car::class.java)
                val speed = MathUtils.getCurrentSpeedRange(car.CurrentSpeed, car.SpeedMax)
                Timber.d("current ratio: ${speed} speed ${car.CurrentSpeed}")
                mCarSpeed.text = car.CurrentSpeed.toString()
                progress.progress= speed
            }
        )
    }

}