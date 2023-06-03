package com.bassamkhafgy.islamy.networking

import com.bassamkhafgy.islamy.data.remote.TimeResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object API {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(TimeApi::class.java)
}

interface TimeApi {
    @GET("timings/{day}")
    fun getPrayerTimes(
        @Path("day") day: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Call<TimeResponse>
}
//   baseUrl("http://api.aladhan.com/v1/timings/1-6-2023?latitude=30.923512&longitude=31.622066")