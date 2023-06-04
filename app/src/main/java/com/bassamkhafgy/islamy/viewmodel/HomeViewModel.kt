package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeSchem
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.convertTo12HourFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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


    private val _prayingTimesLiveData = Channel<TimeSchem>()
    val prayingTimesLiveData = _prayingTimesLiveData.receiveAsFlow()

    private val _lastAddressLiveData = Channel<LastLocation>()
    val lastAddressLiveData = _lastAddressLiveData.receiveAsFlow()


    private val _locationLiveData = MutableStateFlow(repository.getLocationCordination())
    val locationLiveData: StateFlow<Location> = _locationLiveData

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
            } else {

                viewModelScope.launch(Dispatchers.IO) {
                    fagr = repository.getAllStoredTimings().fagr
                    sunrise = repository.getAllStoredTimings().sunrise
                    duhr = repository.getAllStoredTimings().duhr
                    asr = repository.getAllStoredTimings().asr
                    magribe = repository.getAllStoredTimings().magribe
                    fagr = repository.getAllStoredTimings().isha
                }
                //Emit PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _remoteIshaLiveData.emit(isha)
            }

        }
//        return TimeStore(0, fagr, sunrise, duhr, asr, magribe, isha)
    }

    fun getLocation(): Location {
        val location = repository.getLocationCordination()
        viewModelScope.launch {
            _locationLiveData.emit(location)
        }
        return location
    }

    fun getDate(): String {
        return repository.getDate()
    }


    fun getStoredTimings(): TimeSchem {
        fagr = repository.getAllStoredTimings().fagr
        sunrise = repository.getAllStoredTimings().sunrise
        duhr = repository.getAllStoredTimings().duhr
        asr = repository.getAllStoredTimings().asr
        magribe = repository.getAllStoredTimings().magribe
        isha = repository.getAllStoredTimings().isha
        viewModelScope.launch(Dispatchers.IO) {
            //Emit PrayerTimes
            _remoteFagrLiveData.emit(fagr)
            _remoteSunriseLiveData.emit(sunrise)
            _remoteDuhrLiveData.emit(duhr)
            _remoteAsrLiveData.emit(asr)
            _remoteMagribeLiveData.emit(magribe)
            _remoteIshaLiveData.emit(isha)
        }
        return TimeSchem(0, fagr, sunrise, duhr, asr, magribe, isha)

    }


    suspend fun insertLocalTimings(time: TimeSchem) {
        repository.insertToLocalPrayingTimes(time)
    }

    fun insertLastAddress(lastLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLastAddress(lastLocation)
        }
    }

    suspend fun updateLastAddress(lastLocation: String) {
        repository.updateLastAddress(lastLocation)
    }

    suspend fun updateLocalTimings(prayingTime: TimeSchem) {
        repository.updatePrayingTimes(prayingTime)
    }

    fun getLastAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            _lastAddressLiveData.send(LastLocation(0, repository.getLastAddress()))
        }
    }

    fun getAllPrayingTimes() {
        viewModelScope.launch(Dispatchers.IO) {
            _prayingTimesLiveData.send(repository.getAllStoredTimings())
        }
    }


    fun getRemainingTimeToNextPrayer(currentTime: String, prayerTime: String) {
        viewModelScope.launch {
            val durationMillis = repository.getRemainingTimeToNextPrayer(currentTime, prayerTime)

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

    fun checkAddressesValues(): Int {
        return repository.checkAddressesValues()
    }

    fun checkPrayingTimeValues(): Int {
        return repository.checkPrayingTimeValues()
    }
}

