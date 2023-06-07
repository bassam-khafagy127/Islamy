package com.bassamkhafgy.islamy.data.remote


import com.google.gson.annotations.SerializedName

data class Timings(
    @SerializedName("Asr")
    var asr: String?,
    @SerializedName("Dhuhr")
    var dhuhr: String?,
    @SerializedName("Fajr")
    var fajr: String?,
    @SerializedName("Firstthird")
    val firstthird: String?,
    @SerializedName("Imsak")
    val imsak: String?,
    @SerializedName("Isha")
    var isha: String?,
    @SerializedName("Lastthird")
    val lastthird: String?,
    @SerializedName("Maghrib")
    var maghrib: String?,
    @SerializedName("Midnight")
    val midnight: String?,
    @SerializedName("Sunrise")
    var sunrise: String?,
    @SerializedName("Sunset")
    val sunset: String?
)