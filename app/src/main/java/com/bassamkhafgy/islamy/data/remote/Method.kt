package com.bassamkhafgy.islamy.data.remote


import com.google.gson.annotations.SerializedName

data class Method(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("location")
    val location: Location?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("params")
    val params: Params?
)