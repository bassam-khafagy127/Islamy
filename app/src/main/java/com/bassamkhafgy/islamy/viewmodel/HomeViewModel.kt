package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.convertTo12HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {

    private var fagr = ""
    private var sunrise = ""
    private var duhr = ""
    private var asr = ""
    private var magribe = ""
    private var isha = ""


    private val _locationLiveData = MutableStateFlow(repository.getDefaultLocation())
    val locationLiveData: StateFlow<Location> = _locationLiveData

    private val _dateLiveData = MutableStateFlow("")
    val dataLiveData: StateFlow<String> = _dateLiveData

    private val _addressLiveData = MutableStateFlow("")
    val addressLiveData: StateFlow<String> = _addressLiveData

    private val _remoteFagrLiveData = MutableStateFlow("")
    val remoteFagrLiveData: StateFlow<String> = _remoteFagrLiveData

    private val _remoteSunriseLiveData = MutableStateFlow("")
    val remoteSunriseLiveData: StateFlow<String> = _remoteSunriseLiveData

    private val _remoteDuhrLiveData = MutableStateFlow("")
    val remoteDuhrLiveData: StateFlow<String> = _remoteDuhrLiveData

    private val _remoteAsrLiveData = MutableStateFlow("")
    val remoteAsrLiveData: StateFlow<String> = _remoteAsrLiveData

    private val _remoteMagribeLiveData = MutableStateFlow("")
    val remoteMagribeLiveData: StateFlow<String> = _remoteMagribeLiveData

    private val _remoteIshaLiveData = MutableStateFlow("")
    val remoteIshaLiveData: StateFlow<String> = _remoteIshaLiveData

    private val _currentHourLiveData = MutableStateFlow("")
    val currentHourLiveData: StateFlow<String> = _currentHourLiveData

    private var _remainingTimeLiveData = MutableStateFlow("")
    val remainingTimeLiveData: StateFlow<String> = _remainingTimeLiveData

    init {
        getLocation()
    }

    fun getCurrentHour() {
        viewModelScope.launch {
            _currentHourLiveData.emit(
                repository.getCurrentHour()
            )
        }
    }

    fun getRemainingTimeToNextPrayer(currentTime: String, prayerTime: String) {
        viewModelScope.launch {
            repository.getRemainingTimeToNextPrayer(currentTime, prayerTime)
                .collect { remainingTime ->
                    _remainingTimeLiveData.emit(remainingTime)
                    Log.e("REMAiningTime: ", remainingTime)
                }
        }
    }

    fun getAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _addressLiveData.emit(
                repository.getAddress(latitude, longitude)!!
            )
        }
    }

    fun getTimings(day: String, latitude: String, longitude: String) {
        viewModelScope.launch {

            val timings: Response<TimeResponse> = repository.getTimings(day, latitude, longitude)
            if (timings.isSuccessful) {

                fagr = convertTo12HourFormat("${timings.body()?.data?.timings?.fajr}")
                sunrise = convertTo12HourFormat("${timings.body()?.data?.timings?.sunrise}")
                duhr = convertTo12HourFormat("${timings.body()?.data?.timings?.dhuhr}")
                asr = convertTo12HourFormat("${timings.body()?.data?.timings?.asr}")
                magribe = convertTo12HourFormat("${timings.body()?.data?.timings?.maghrib}")
                isha = convertTo12HourFormat("${timings.body()?.data?.timings?.isha}")

                //Emit PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _remoteIshaLiveData.emit(isha)

            }

        }
    }

    fun getLocation() {
        viewModelScope.launch {
            _locationLiveData.emit(
                repository.getLocationLongitude()
            )
        }
    }

    fun getDate() {
        viewModelScope.launch {
            _dateLiveData.emit(repository.getDate())
        }
    }

}
