package com.bassamkhafgy.islamy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.SearchRepository
import com.bassamkhafgy.islamy.utill.convertTo12HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

private var fagr = ""
private var sunrise = ""
private var duhr = ""
private var asr = ""
private var magribe = ""
private var isha = ""
private var address = ""

private var hijriDate = ""

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

private val _addressLiveData = MutableStateFlow(LastLocation(0, ""))
val addressLiveData: StateFlow<LastLocation> = _addressLiveData


@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {
    fun getTodayRemoteTimings(day: String, latitude: String, longitude: String) {
        viewModelScope.launch {

            val timings: Response<TimeResponse> =
                repository.getTodayTimings(day, latitude, longitude)
            if (timings.isSuccessful) {
                fagr = convertTo12HourFormat("${timings.body()?.data?.timings?.fajr}")
                sunrise = convertTo12HourFormat("${timings.body()?.data?.timings?.sunrise}")
                duhr = convertTo12HourFormat("${timings.body()?.data?.timings?.dhuhr}")
                asr = convertTo12HourFormat("${timings.body()?.data?.timings?.asr}")
                magribe = convertTo12HourFormat("${timings.body()?.data?.timings?.maghrib}")
                isha = convertTo12HourFormat("${timings.body()?.data?.timings?.isha}")
                address =
                    repository.getAddress(latitude.toDouble(), longitude.toDouble()).toString()
                hijriDate = "${timings.body()?.data?.date?.hijri?.date}"
//                Emitresponse value PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _remoteIshaLiveData.emit(isha)
                _addressLiveData.emit(LastLocation(0, address))

            } else {
//                response IsFaild

            }

        }
    }

    fun getDay(): String {
        return repository.getDate()
    }

}