package com.seigneur.gauvain.needforstream.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.ui.list.MainViewModel
import timber.log.Timber

class DetailsFragment: Fragment() {

    val mMainViewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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