package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.PrayerTime
import com.bassamkhafgy.islamy.data.remote.Timings
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.Resource
import com.bassamkhafgy.islamy.utill.getNextAzanTitle
import com.bassamkhafgy.islamy.utill.getTime12hrsFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private val _flowLocationData: MutableStateFlow<Resource<Location>> =
        MutableStateFlow(Resource.Unspecified())
    val flowLocationData: StateFlow<Resource<Location>> = _flowLocationData

    private val _prayingTimingsFlow: MutableStateFlow<Resource<PrayerSchedule>> =
        MutableStateFlow(Resource.Unspecified())
    val prayingTimingsFlow: StateFlow<Resource<PrayerSchedule>> = _prayingTimingsFlow

    private val _liveAddressFlow: MutableStateFlow<String> = MutableStateFlow("")
    val liveAddressFlow: StateFlow<String> = _liveAddressFlow

    private val _nextPrayerTitle: MutableStateFlow<PrayerTime> =
        MutableStateFlow(PrayerTime("", ""))
    val nextPrayerTitle: StateFlow<PrayerTime> = _nextPrayerTitle


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
            _prayingTimingsFlow.emit(timingsResponse)
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
            _prayingTimingsFlow.emit(timingsResponse)
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
            _prayingTimingsFlow.emit(timingsCached)
        }
    }

    //getNextPrayer Title
    fun getNextPrayer(prayers: List<PrayerTime>) {
        viewModelScope.launch {
            _nextPrayerTitle.emit(repository.getNextPrayer(prayers))
        }
    }

}
