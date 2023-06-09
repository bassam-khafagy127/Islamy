package com.bassamkhafgy.islamy.repository

import android.content.Context
import android.location.Location
import com.bassamkhafgy.islamy.data.database.IslamyAppDataBase
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.PrayerTime
import com.bassamkhafgy.islamy.data.local.StoringAddress
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.networking.TimeApiService
import com.bassamkhafgy.islamy.utill.Resource
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getLocationLatitudeLongitude
import com.bassamkhafgy.islamy.utill.getNextAzanTitle
import com.bassamkhafgy.islamy.utill.getTime12hrsFormat
import com.bassamkhafgy.islamy.utill.getTimeForApi
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val context: Context,
    private var fusedLocationProviderClient: FusedLocationProviderClient,
    private val timeApiService: TimeApiService,
    private val timingsDataBase: IslamyAppDataBase
) {


    //getLocation Coordination witGPs
    suspend fun getLocationCoordination(): SharedFlow<Resource<Location>> {
        return getLocationLatitudeLongitude(fusedLocationProviderClient)
    }

    private var currentTimings = PrayerSchedule(0, "", "", "", "", "", "")

    suspend fun getRemoteTimings(
        day: String,
        latitude: String,
        longitude: String,
    ): Resource<PrayerSchedule> {
        val timings = timeApiService.getPrayerTimes(day, latitude, longitude)
        val prayerTimesResponse = timings.body()?.data?.timings

        if (timings.isSuccessful) {
            currentTimings.fajr = getTime12hrsFormat(prayerTimesResponse?.fajr.toString())
            currentTimings.sunrise = getTime12hrsFormat(prayerTimesResponse?.sunrise.toString())
            currentTimings.dhuhr = getTime12hrsFormat(prayerTimesResponse?.dhuhr.toString())
            currentTimings.asr = getTime12hrsFormat(prayerTimesResponse?.asr.toString())
            currentTimings.maghrib = getTime12hrsFormat(prayerTimesResponse?.maghrib.toString())
            currentTimings.isha = getTime12hrsFormat(prayerTimesResponse?.isha.toString())

            //drop old data
            dropOldData()
            //insert to local database
            insertUpdateDayTimings(currentTimings)
            return Resource.Success(currentTimings)

        } else {
            return Resource.Error(timings.errorBody().toString())
        }
    }

    //getNExtAndLastDay
    suspend fun getNextAndLastDayTimings(
        day: String,
        latitude: String,
        longitude: String,
    ): Resource<PrayerSchedule> {
        val timings = timeApiService.getPrayerTimes(day, latitude, longitude)
        val prayerTimesResponse = timings.body()?.data?.timings

        if (timings.isSuccessful) {
            currentTimings.fajr = getTime12hrsFormat(prayerTimesResponse?.fajr.toString())
            currentTimings.sunrise = getTime12hrsFormat(prayerTimesResponse?.sunrise.toString())
            currentTimings.dhuhr = getTime12hrsFormat(prayerTimesResponse?.dhuhr.toString())
            currentTimings.asr = getTime12hrsFormat(prayerTimesResponse?.asr.toString())
            currentTimings.maghrib = getTime12hrsFormat(prayerTimesResponse?.maghrib.toString())
            currentTimings.isha = getTime12hrsFormat(prayerTimesResponse?.isha.toString())
        }
        return Resource.Success(currentTimings)
    }

    //getAddress
    fun getAddressFromLatLong(latitude: String, longitude: String): String {
        return getAddressGeocoder(context, latitude.toDouble(), longitude.toDouble())!!
    }

    //getTodayDate
    fun getTimeForApiFormat(): String {
        return getTimeForApi()
    }


    //insert Address to dataBase
     suspend fun insertAddress(address: StoringAddress) {
        timingsDataBase.addressDao().insertAddress(address)
    }

    //update address
     suspend fun dropOldAddressData() {
        timingsDataBase.addressDao().deleteOldData()
    }

    //getCachedAddress from DataBase
    fun getCachedAddress(): StoringAddress {
        return timingsDataBase.addressDao().getAddress()
    }


    //insert Today Timings to dataBase
    private suspend fun insertUpdateDayTimings(timings: PrayerSchedule) {
        timingsDataBase.timingsDao().insertTimings(timings)
    }

    //updateTimings
    private suspend fun dropOldData() {
        timingsDataBase.timingsDao().deleteOldData()
    }

    //getDataFromDataBase
    fun getCachedTimings(): Resource<PrayerSchedule> {
        return Resource.Success(timingsDataBase.timingsDao().getDayTimings())
    }

    //getNextPrayer Title
    fun getNextPrayer(prayers: List<PrayerTime>): PrayerTime {
        return getNextAzanTitle(prayers)
    }

}