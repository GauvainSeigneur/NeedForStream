package com.seigneur.gauvain.needforstream.ui.list

import com.seigneur.gauvain.needforstream.data.model.Car

interface CarListCallback {

    fun onStartClicked(car: Car?, position: Int)
}
