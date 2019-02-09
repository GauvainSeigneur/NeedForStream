package com.seigneur.gauvain.needforstream.data.model

import com.google.gson.annotations.SerializedName

data class Car(
    @SerializedName("Brand")
    var Brand:String,
    @SerializedName("Name")
    var Name:String,
    @SerializedName("SpeedMax")
    var SpeedMax: Int,
    @SerializedName("Cv")
    var Cv: Int,
    @SerializedName("CurrentSpeed")
    var CurrentSpeed: Int)

