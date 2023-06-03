package com.bassamkhafgy.islamy.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.networking.API
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getSystemDate
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Call
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val context: Context,
    private var fusedLocationProviderClient: FusedLocationProviderClient,
) {

    init {
        getLocationLongitude()
    }

    private val _location: Location = Location("")

    fun getTimings(day: String, latitude: String, longitude: String): Call<TimeResponse> {
        return API.apiService.getPrayerTimes(day, latitude, longitude)
    }

    fun getAddress(latitude: Double, longitude: Double): String? {
        return getAddressGeocoder(context, latitude, longitude)
    }

    fun getDate(): String {
        return getSystemDate()
    }

    fun getDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = Constants.Location.CAIRO_LAT
        defaultLocation.longitude = Constants.Location.CAIRO_LONG
        return defaultLocation
    }


    @SuppressLint("MissingPermission")
    fun getLocationLongitude(): Location {
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {

            if (it != null) {
                _location.latitude = it.latitude
                _location.longitude = it.longitude
                _location.altitude = it.altitude

            }
        }.addOnFailureListener {
//            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_LONG).show()
            Log.e("SSSSSSS", it.message.toString())
        }
        return _location
    }

}