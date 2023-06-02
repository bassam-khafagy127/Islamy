package com.bassamkhafgy.islamy.data.remote


import com.google.gson.annotations.SerializedName

data class Params(
    @SerializedName("Fajr")
    val fajr: Double?,
    @SerializedName("Isha")
    val isha: Double?
)