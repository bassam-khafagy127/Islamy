package com.bassamkhafgy.islamy.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.bassamkhafgy.islamy.data.database.TimingsDataBase
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeStore
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.networking.TimeApiService
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.ERROR_TAG
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getPrayerRemainingTime
import com.bassamkhafgy.islamy.utill.getSystemCurrentTime
import com.bassamkhafgy.islamy.utill.getSystemDate
import com.google.android.gms.location.FusedLocationProviderClient
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val context: Context,
    private var fusedLocationProviderClient: FusedLocationProviderClient,
    private val timeApiService: TimeApiService,
    private val timingsDataBase: TimingsDataBase
) {
    init {
        getLocationCordination()
    }

    private val _location: Location = Location("")

    suspend fun getTimings(
        day: String,
        latitude: String,
        longitude: String
    ): Response<TimeResponse> {
        return timeApiService.getPrayerTimes(day, latitude, longitude)
    }

    fun getAddress(latitude: Double, longitude: Double): String? {
        return getAddressGeocoder(context, latitude, longitude)
    }

    fun getDate() = getSystemDate()

    fun getCurrentHour(): String {
        return getSystemCurrentTime()
    }

    fun getRemainingTimeToNextPrayer(currentTime: String, prayerTime: String): Long {
        return getPrayerRemainingTime(currentTime, prayerTime)
    }


    fun getDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = Constants.Location.CAIRO_LAT
        defaultLocation.longitude = Constants.Location.CAIRO_LONG
        return defaultLocation
    }


    @SuppressLint("MissingPermission")
    fun getLocationCordination(): Location {
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {

            if (it != null) {
                _location.latitude = it.latitude
                _location.longitude = it.longitude
                _location.altitude = it.altitude

            }
        }.addOnFailureListener {
//            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_LONG).show()
            Log.e(ERROR_TAG, it.message.toString())
        }
        return _location
    }

    fun insertLastAddress(lastLocation: String) {
        timingsDataBase.locationDao().insertAddress(LastLocation(0,lastLocation))
    }

    fun insertToLocalPrayingTimes(time: TimeStore) {
        timingsDataBase.timingsDao().insertTimings(time)
    }

    fun updateLastAddress(lastLocation: String) {
        timingsDataBase.locationDao().updateAddress(LastLocation(0,lastLocation))
    }

    fun updatePrayingTimes(lastPrayingTime: TimeStore) {
        timingsDataBase.timingsDao().updateTimings(lastPrayingTime)
    }

    fun getLastAddress(): String {
        val size = timingsDataBase.locationDao().getLastAddress().size
        return timingsDataBase.locationDao().getLastAddress()[size - 1].location
    }

    fun getAllStoredTimings(): TimeStore {
        val size = timingsDataBase.timingsDao().getAllTimings().size
        return timingsDataBase.timingsDao().getAllTimings()[size - 1]
    }
}