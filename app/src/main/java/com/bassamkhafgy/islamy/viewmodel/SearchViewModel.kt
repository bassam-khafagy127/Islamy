package com.bassamkhafgy.islamy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.data.remote.Timings
import com.bassamkhafgy.islamy.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    private val currentTimings =
        Timings(
            "", " ",
            "", "",
            "", "",
            "", "",
            "", "",
            ""
        )

    private var address = ""

    private val _prayingTimingsFlow: MutableStateFlow<Timings> = MutableStateFlow(currentTimings)
    val prayingTimingsFlow: StateFlow<Timings> = _prayingTimingsFlow

    private val _addressLiveData = MutableStateFlow("")
    val addressLiveData: StateFlow<String> = _addressLiveData

    fun getTodayRemoteTimings(day: String, latitude: String, longitude: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val timings: Response<TimeResponse> =
                repository.getRemoteTimings(day, latitude, longitude)
            if (timings.isSuccessful) {
                val timeResponse = timings.body()?.data?.timings
                _prayingTimingsFlow.emit(
                    Timings(
                        timeResponse?.asr,
                        timeResponse?.dhuhr,
                        timeResponse?.fajr,
                        "",
                        "",
                        timeResponse?.isha,
                        "",
                        timeResponse?.maghrib,
                        "",
                        timeResponse?.sunrise,
                        ""
                    )
                )
                address =
                    repository.getAddress(latitude.toDouble(), longitude.toDouble()).toString()

                _addressLiveData.emit(address)
            } else {
//                response IsFaild

            }

        }
    }

    fun getDay(): String {
        return repository.getDate()
    }

}