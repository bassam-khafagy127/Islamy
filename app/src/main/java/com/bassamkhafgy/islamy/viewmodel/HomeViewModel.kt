package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeStore
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.convertTo12HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _latLongLiveData = MutableStateFlow(LastLocation(0, "", ""))
    val latLongLiveData: StateFlow<LastLocation> = _latLongLiveData

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

    private val _remainingTimeLiveData = MutableStateFlow("")
    val remainingTimeLiveData: StateFlow<String> = _remainingTimeLiveData


    init {
        getLocation()
    }

    fun getCurrentHour() {
        viewModelScope.launch {
            _currentHourLiveData.emit(
                repository.getCurrentHour()
            )
        }.start()
    }

    //Stable


    fun getAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _addressLiveData.emit(
                repository.getAddress(latitude, longitude)!!
            )
        }
    }

    fun getTimings(day: String, latitude: String, longitude: String): TimeStore {
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
            } else {
                fagr = repository.getStoredTimings().fagr
                sunrise = repository.getStoredTimings().sunrise
                duhr = repository.getStoredTimings().duhr
                asr = repository.getStoredTimings().asr
                magribe = repository.getStoredTimings().magribe
                fagr = repository.getStoredTimings().isha

                //Emit PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _remoteIshaLiveData.emit(isha)

            }

        }
        return TimeStore(0, fagr, sunrise, duhr, asr, magribe, isha)
    }

    fun getLocation() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun insertLocalTimings(time: TimeStore) {
        repository.insertToLocalRemaining(time)
    }

    fun getStoredTimings(): TimeStore {
        fagr = repository.getStoredTimings().fagr
        sunrise = repository.getStoredTimings().sunrise
        duhr = repository.getStoredTimings().duhr
        asr = repository.getStoredTimings().asr
        magribe = repository.getStoredTimings().magribe
        isha = repository.getStoredTimings().isha
        viewModelScope.launch(Dispatchers.IO) {
            //Emit PrayerTimes
            _remoteFagrLiveData.emit(fagr)
            _remoteSunriseLiveData.emit(sunrise)
            _remoteDuhrLiveData.emit(duhr)
            _remoteAsrLiveData.emit(asr)
            _remoteMagribeLiveData.emit(magribe)
            _remoteIshaLiveData.emit(isha)
        }
        return TimeStore(0, fagr, sunrise, duhr, asr, magribe, isha)

    }

    fun insertLastLocation(lastLocation: LastLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLocation(lastLocation)
        }
    }

    fun getLastLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _latLongLiveData.emit(repository.getLastLocation())
        }
    }


    fun getRemainingTimeToNextPrayer(currentTime: String, prayerTime: String) {
        viewModelScope.launch {
            val durationMillis = repository.getRemainingTimeToNextPrayer(currentTime, "6:10")

            val countDownTimer = object : CountDownTimer(durationMillis, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000 % 60
                    val minutes = millisUntilFinished / (1000 * 60) % 60
                    val hours = millisUntilFinished / (1000 * 60 * 60)

                    val remainingTime = "$hours:$minutes"
                    viewModelScope.launch {
                        _remainingTimeLiveData.emit(remainingTime)

                    }
                }

                override fun onFinish() {
                    viewModelScope.launch {
                        _remainingTimeLiveData.emit("0:0")

                    }
                }

            }
            countDownTimer.start()
        }
    }
}
