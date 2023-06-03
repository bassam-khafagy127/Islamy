package com.bassamkhafgy.islamy.networking

import com.bassamkhafgy.islamy.data.remote.TimeResponse
import retrofit2.Call
import retrofit2.Response
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
    val timeApiService = retrofit.create(TimeApiService::class.java)
}
