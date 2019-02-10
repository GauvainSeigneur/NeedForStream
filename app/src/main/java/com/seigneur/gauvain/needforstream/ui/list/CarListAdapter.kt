package com.seigneur.gauvain.needforstream.ui.list

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seigneur.gauvain.needforstream.R
import com.seigneur.gauvain.needforstream.data.model.Car

class CarListAdapter(private val data: MutableList<Car>,
                     private var mCallback: CarListCallback
)
    : RecyclerView.Adapter<CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view, mCallback)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val item = data[position]
        holder.bindTo(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        while (itemCount > 0) {
            data.clear()
        }
    }

}
