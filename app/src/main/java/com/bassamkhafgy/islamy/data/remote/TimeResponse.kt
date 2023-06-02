package com.bassamkhafgy.islamy.data.remote


import com.google.gson.annotations.SerializedName

data class TimeResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("status")
    val status: String?
)