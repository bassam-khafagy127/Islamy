package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {


    private val _flowLocationData: MutableStateFlow<Location?> = MutableStateFlow(Location(""))
    val flowLocationData: StateFlow<Location?> = _flowLocationData

    private val dummyTime =
        PrayerSchedule(0, "4:10", "6:05 ", "12:54 PM", "4:30 PM", "7:55 PM", "9:27 PM")

    private val _prayingTimingsFlow: MutableStateFlow<PrayerSchedule> = MutableStateFlow(dummyTime)
    val prayingTimingsFlow: StateFlow<PrayerSchedule> = _prayingTimingsFlow

    private val _liveAddressFlow: MutableStateFlow<String> = MutableStateFlow("")
    val liveAddressFlow: StateFlow<String> = _liveAddressFlow

    private val _remainingToNextPrayerFlow: MutableStateFlow<String> = MutableStateFlow("")
    val remainingToNextPrayerFlow: StateFlow<String> = _remainingToNextPrayerFlow


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
            val timingsResponse = repository.getCachedTimings()
            _prayingTimingsFlow.emit(timingsResponse)
        }
    }


}



