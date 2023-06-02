package com.bassamkhafgy.islamy.networking

import com.bassamkhafgy.islamy.data.remote.TimeResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

object API {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/v1/timings/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(TimeApi::class.java)
}

interface TimeApi {
    @GET("1-6-2023?latitude=30.923512&longitude=31.622066")
    fun getPrayerTimes(): Call<TimeResponse>
}