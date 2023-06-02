package com.bassamkhafgy.islamy.repository

import android.content.Context
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.networking.API
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getSystemDate
import retrofit2.Call
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val context: Context
) {
    fun getTimings(): Call<TimeResponse> {
       return API.apiService.getPrayerTimes()
    }

    fun getAddress(latitude: Double, longitude: Double): String? {
        return getAddressGeocoder(context, latitude, longitude)
    }

    fun getDate(): String {
        return getSystemDate()
    }
}