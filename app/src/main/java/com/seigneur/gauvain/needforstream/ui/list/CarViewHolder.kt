package com.seigneur.gauvain.needforstream.ui.list

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car

class CarViewHolder(itemView: View, private var mCallback: CarListCallback) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener  {
    private var mCar:Car?=null

    private val mCarBrand by lazy {
        itemView.findViewById<TextView>(R.id.mCarBrand)
    }

    private val mCarName by lazy {
        itemView.findViewById<TextView>(R.id.mCarName)
    }

    private val mCarCv by lazy {
        itemView.findViewById<TextView>(R.id.mCarCV)
    }

    private val mCarMaxSpeed by lazy {
        itemView.findViewById<TextView>(R.id.mCarSpeedMax)
    }

    private val mStartCar by lazy {
        itemView.findViewById<Button>(R.id.mStartCar)
    }

    fun bindTo(car:Car) {
        mCar=car
        mCarBrand.text =    car.Brand
        mCarName.text =     car.Name
        mCarCv.text =     "${car.Cv} Cv"
        mCarCv.text =     "Max Speed: ${car.SpeedMax} Km/h"

        mStartCar.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        mCallback.onStartClicked(mCar, adapterPosition)
    }

}
