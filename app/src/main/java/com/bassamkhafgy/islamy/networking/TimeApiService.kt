package com.bassamkhafgy.islamy.networking

import com.bassamkhafgy.islamy.data.remote.TimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TimeApiService {
    @GET("timings/{day}")
    suspend fun getPrayerTimes(
        @Path("day") day: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String
    ): Response<TimeResponse>

}