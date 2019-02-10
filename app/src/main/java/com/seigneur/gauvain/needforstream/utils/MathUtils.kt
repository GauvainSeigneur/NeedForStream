package com.seigneur.gauvain.needforstream.utils

object MathUtils {

    @JvmStatic
    fun getCurrentSpeedRange(currentSpeed: Float, maxSpeed:Int) :Int{
        return ((currentSpeed.toInt() * 100.0f) / maxSpeed).toInt()
    }

}