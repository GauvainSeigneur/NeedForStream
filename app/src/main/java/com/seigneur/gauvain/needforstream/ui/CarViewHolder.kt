package com.seigneur.gauvain.needforstream.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car
import kotlinx.android.synthetic.main.item_car.view.*

class CarViewHolder(itemView: View, private var mCallback: CarListCallback) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener  {
    private var mCar:Car?=null

    private val mCarBrand by lazy {
        itemView.findViewById<TextView>(R.id.mCarBrand)
    }

    fun bindTo(car:Car) {
        mCar=car
        mCarBrand.text = car.Brand
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        mCallback.onShotDraftClicked(mCar, adapterPosition)
    }

}
