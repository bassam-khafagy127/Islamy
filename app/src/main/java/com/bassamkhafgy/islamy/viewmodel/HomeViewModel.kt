package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.remote.Timings
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.getTime12hrsFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private val currentTimings =
        Timings(
            "", " ",
            "", "",
            "", "",
            "", "",
            "", "",
            ""
        )
    private val _flowLocationData: MutableStateFlow<Location?> = MutableStateFlow(Location(""))
    val flowLocationData: StateFlow<Location?> = _flowLocationData

    private val _prayingTimingsFlow: MutableStateFlow<Timings> = MutableStateFlow(currentTimings)
    val prayingTimingsFlow: StateFlow<Timings> = _prayingTimingsFlow

    private val _liveAddressFlow: MutableStateFlow<String> = MutableStateFlow("")
    val liveAddressFlow: StateFlow<String> = _liveAddressFlow


    //GetLocationLatLong
    fun getLocationCoordination() {
        viewModelScope.launch {
            repository.getLocationCoordination().collect {
                _flowLocationData.emit(it)
            }
        }
    }

    //getRemoteTimings
    fun getRemoteTimings(
        day: String,
        latitude: String,
        longitude: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val timingsResponse = repository.getRemoteTimings(day, latitude, longitude)
            _prayingTimingsFlow.emit(
                Timings(
                    timingsResponse.asr,
                    timingsResponse.dhuhr,
                    timingsResponse.fajr,
                    "",
                    "",
                    timingsResponse.isha,
                    "",
                    timingsResponse.maghrib,
                    "",
                    timingsResponse.sunrise,
                    ""
                )
            )
        }
    }

    //getNextLastTimings
    fun getNextAndLastDayTimings(
        day: String,
        latitude: String,
        longitude: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val timingsResponse = repository.getNextAndLastDayTimings(day, latitude, longitude)
            _prayingTimingsFlow.emit(
                Timings(
                    timingsResponse.asr,
                    timingsResponse.dhuhr,
                    timingsResponse.fajr,
                    "",
                    "",
                    timingsResponse.isha,
                    "",
                    timingsResponse.maghrib,
                    "",
                    timingsResponse.sunrise,
                    ""
                )
            )
        }
    }

    //getTimeWithApiFormat
    fun getTimeForApi(): String {
        return repository.getTimeForApiFormat()
    }

    //getUserAddress(City:Country)
    fun getUserAddress(latitude: String, longitude: String): String {
        val address = repository.getAddressFromLatLong(latitude, longitude)
        viewModelScope.launch {
            _liveAddressFlow.emit(address)
        }
        return address
    }

    //getFromDataBase
    fun getCachedTimings() {
        viewModelScope.launch(Dispatchers.IO) {
            val timingsCached = repository.getCachedTimings()
            _prayingTimingsFlow.emit(
                Timings(
                    timingsCached.asr,
                    timingsCached.dhuhr,
                    timingsCached.fajr,
                    "",
                    "",
                    timingsCached.isha,
                    "",
                    timingsCached.maghrib,
                    "",
                    timingsCached.sunrise,
                    ""
                )
            )
        }
    }

}
