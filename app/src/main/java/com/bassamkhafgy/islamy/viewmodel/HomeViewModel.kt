package com.bassamkhafgy.islamy.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.TimeShow
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.convertTo12HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
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

    private val _dateLiveData = MutableStateFlow("")
    val dataLiveData: StateFlow<String> = _dateLiveData

    private val _addressLiveData = MutableStateFlow("")
    val addressLiveData: StateFlow<String> = _addressLiveData

    private val _fagrLiveData = MutableStateFlow("")
    val fagrLiveData: StateFlow<String> = _fagrLiveData

    private val _sunriseLiveData = MutableStateFlow("")
    val sunriseLiveData: StateFlow<String> = _sunriseLiveData

    private val _duhrLiveData = MutableStateFlow("")
    val duhrLiveData: StateFlow<String> = _duhrLiveData

    private val _asrLiveData = MutableStateFlow("")
    val asrLiveData: StateFlow<String> = _asrLiveData

    private val _magribeLiveData = MutableStateFlow("")
    val magribeLiveData: StateFlow<String> = _magribeLiveData

    private val _ishaLiveData = MutableStateFlow("")
    val ishaLiveData: StateFlow<String> = _ishaLiveData


    fun getAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _addressLiveData.emit(
                repository.getAddress(latitude, longitude)!!
            )
        }
    }

    fun getTimings() {
        repository.getTimings().enqueue(object : Callback<TimeResponse> {
            override fun onResponse(call: Call<TimeResponse>, response: Response<TimeResponse>) {

                fagr = convertTo12HourFormat("${response.body()?.data?.timings?.fajr}")
                sunrise = convertTo12HourFormat("${response.body()?.data?.timings?.sunrise}")
                duhr = convertTo12HourFormat("${response.body()?.data?.timings?.dhuhr}")
                asr = convertTo12HourFormat("${response.body()?.data?.timings?.asr}")
                magribe = convertTo12HourFormat("${response.body()?.data?.timings?.maghrib}")
                isha = convertTo12HourFormat("${response.body()?.data?.timings?.isha}")

                viewModelScope.launch {
                    _fagrLiveData.emit(fagr)
                    _sunriseLiveData.emit(sunrise)
                    _duhrLiveData.emit(duhr)
                    _asrLiveData.emit(asr)
                    _magribeLiveData.emit(magribe)
                    _ishaLiveData.emit(isha)
                }
            }

            override fun onFailure(call: Call<TimeResponse>, t: Throwable) {
                //getLocal
            }

        })
    }

    fun getDate() {
        viewModelScope.launch {
            _dateLiveData.emit(repository.getDate())
        }
    }
}
