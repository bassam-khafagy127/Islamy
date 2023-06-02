package com.bassamkhafgy.islamy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _dateLiveData = MutableLiveData<String>()
    val dataLiveData: LiveData<String> = _dateLiveData

    private val _addressLiveData = MutableLiveData<String>()
    val addressLiveData: LiveData<String> = _addressLiveData


    fun getAddress(latitude: Double, longitude: Double){
        viewModelScope.launch {
            _addressLiveData.postValue(
                homeRepository.getAddress(latitude, longitude)
            )
        }
    }

    fun getDate() {
        viewModelScope.launch {
            _dateLiveData.postValue(homeRepository.getDate())
        }
    }
}