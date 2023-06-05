package com.bassamkhafgy.islamy.viewmodel

import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.PrayerScheduleConverter
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.repository.HomeRepository
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.ERROR_TAG
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
    private var address = ""


    private val _prayingTimesLiveData = Channel<PrayerSchedule>()
    val prayingTimesLiveData = _prayingTimesLiveData.receiveAsFlow()

    private val _lastAddressLiveData = Channel<LastLocation>()
    val lastAddressLiveData = _lastAddressLiveData.receiveAsFlow()


    private val _locationLiveData = MutableStateFlow(repository.getLocationCoordination())
    val locationLiveData: StateFlow<Location> = _locationLiveData

    private val _addressLiveData = MutableStateFlow(LastLocation(0, ""))
    val addressLiveData: StateFlow<LastLocation> = _addressLiveData

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

    private val _nextAzanTimeLiveData = MutableStateFlow("")
    val nextAzanTimeLiveData: StateFlow<String> = _nextAzanTimeLiveData


    init {
        getLocation()
    }

    //getCurrentHour FromSystem
    fun getCurrentHour() {
        viewModelScope.launch {
            _currentHourLiveData.emit(
                repository.getCurrentHour()
            )
        }.start()
    }

    //Stable

    //getAddressFromGeoCoder
    fun getAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _addressLiveData.emit(
                LastLocation(0, repository.getAddress(latitude, longitude)!!)
            )
        }
    }

    //getTodayTimingsFromApi
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

                //Emitresponse value PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _remoteIshaLiveData.emit(isha)
                _addressLiveData.emit(LastLocation(0, address))

                launch (Dispatchers.IO){
                    val prayTimes =
                        PrayerSchedule(
                            0,
                            fagr,
                            sunrise,
                            duhr,
                            asr,
                            magribe,
                            isha,
                            address
                        )

                    if (checkPrayingTimeValues() > 0) {
                        repository.updatePrayingTimes(prayTimes)
                    } else {
                        repository.insertToLocalPrayingTimes(prayTimes)
                    }
                }
            } else {
//                response Is Failed
                viewModelScope.launch(Dispatchers.IO) {
                    //getFromDAtaBase
                    fagr = repository.getAllStoredTimings().fajr
                    sunrise = repository.getAllStoredTimings().sunrise
                    duhr = repository.getAllStoredTimings().dhuhr
                    asr = repository.getAllStoredTimings().asr
                    magribe = repository.getAllStoredTimings().maghrib
                    fagr = repository.getAllStoredTimings().isha
                    address = repository.getAllStoredTimings().address
                }
                //Emit PrayerTimes
                _remoteFagrLiveData.emit(fagr)
                _remoteSunriseLiveData.emit(sunrise)
                _remoteDuhrLiveData.emit(duhr)
                _remoteAsrLiveData.emit(asr)
                _remoteMagribeLiveData.emit(magribe)
                _addressLiveData.emit(LastLocation(0, address))


            }

        }
    }

    fun getLocation(): Location {
        val location = repository.getLocationCoordination()
        viewModelScope.launch {
            _locationLiveData.emit(location)
        }
        return location
    }

    //DateFromSystem
    fun getDate(): String {
        return repository.getDate()
    }

    //localDataBaseTodayTimings
    fun getStoredTimings(): PrayerSchedule {
        fagr = repository.getAllStoredTimings().fajr
        sunrise = repository.getAllStoredTimings().sunrise
        duhr = repository.getAllStoredTimings().dhuhr
        asr = repository.getAllStoredTimings().asr
        magribe = repository.getAllStoredTimings().maghrib
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
        return PrayerSchedule(0, fagr, sunrise, duhr, asr, magribe, isha, "")

    }


    //DataBase isert Today Timings
    suspend fun insertLocalTimings(time: PrayerSchedule) {
        repository.insertToLocalPrayingTimes(time)
    }

    //DataBase insert today Address
    fun insertLastAddress(lastLocation: LastLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLastAddress(lastLocation)
        }
    }

    //DataBase update today Address
    suspend fun updateLastAddress(lastLocation: LastLocation) {
        repository.updateLastAddress(lastLocation)
    }


    //DataBase update Today Timing
    suspend fun updateLocalTimings(prayingTime: PrayerSchedule) {
        repository.updatePrayingTimes(prayingTime)
    }


    //DataBase get Today address
    fun getLastAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            _lastAddressLiveData.send(LastLocation(0, repository.getLocalLastAddress()))
        }
    }

    fun getAllPrayingTimes() {
        viewModelScope.launch(Dispatchers.IO) {
            _prayingTimesLiveData.send(repository.getAllStoredTimings())
        }
    }


    //Get Remaining Time And Next Azan
    fun getRemainingTimeToNextPrayer(currentTime: String, prayerTime: String, nextAzan: String) {
        viewModelScope.launch {

            Log.d(ERROR_TAG, "cur:$currentTime P:$prayerTime az$nextAzan")
            val durationMillis = repository.getRemainingTimeToNextPrayer(currentTime, prayerTime)

            val countDownTimer = object : CountDownTimer(durationMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
//                         val seconds = millisUntilFinished / 1000 % 60
                    val minutes = millisUntilFinished / (1000 * 60) % 60
                    val hours = millisUntilFinished / (1000 * 60 * 60)

                    val remainingTime = "H:$hours\nM:$minutes"
                    viewModelScope.launch {
                        _remainingTimeLiveData.emit(remainingTime)
                        _nextAzanTimeLiveData.emit(nextAzan)
                    }
                }

                override fun onFinish() {
                    viewModelScope.launch {
                        _remainingTimeLiveData.emit("0:0")
                        _nextAzanTimeLiveData.emit("Prayer Now")

                    }
                }

            }
            countDownTimer.start()
        }
    }

    //Check AddressValue in DataBase
    fun checkAddressesValues(): Int {
        return repository.checkAddressesValues()
    }

    //DataBase checkPrayingTimeValues Today isnot null
    fun checkPrayingTimeValues(): Int {
        return repository.checkPrayingTimeValues()
    }


    //getNextAzanValue
    fun getNextPrayerTimeTitle(prayerTime: PrayerScheduleConverter): Pair<String, String> {
        val e = repository.getNextPrayerTimeTitle(prayerTime)
        Log.d(ERROR_TAG + "eeee:", e.second)
        return repository.getNextPrayerTimeTitle(prayerTime)
    }
}

//
//    //get REmote NextDay
//    fun getNextPrayerTurnWithConnection(prayerSchedule: PrayerSchedule) {
//
//    }
//
//
//    //get Remote lastDay
//    fun getLastPrayerTurnWithoutConnection() {
//
//    }
