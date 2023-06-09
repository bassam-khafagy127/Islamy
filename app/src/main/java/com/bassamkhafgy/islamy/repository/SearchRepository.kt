package com.bassamkhafgy.islamy.repository

import android.content.Context
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.networking.TimeApiService
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getSystemDate
import retrofit2.Response
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val timeApiService: TimeApiService,
    private val context: Context
) {

    // getRemoteTimings
    suspend fun getRemoteTimings(
        day: String,
        latitude: String,
        longitude: String
    ): Response<TimeResponse> {
        return timeApiService.getPrayerTimes(day, latitude, longitude)
    }

    fun getDate() = getSystemDate()

    //    getGeoAddress
    fun getAddress(latitude: Double, longitude: Double): String? {
        return getAddressGeocoder(context, latitude, longitude)
    }
}