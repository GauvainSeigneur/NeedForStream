package com.seigneur.gauvain.needforstream.ui

import com.seigneur.gauvain.needforstream.data.model.Car

interface CarListCallback {

    fun onShotDraftClicked(car: Car?, position: Int)
}
