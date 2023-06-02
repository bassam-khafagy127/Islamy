package com.bassamkhafgy.islamy.repository

import android.content.Context
import android.widget.Toast
import com.bassamkhafgy.islamy.utill.getAddressGeocoder
import com.bassamkhafgy.islamy.utill.getSystemDate
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val context: Context
) {
    fun getAddress(latitude: Double, longitude: Double): String? {
        return getAddressGeocoder(context, latitude, longitude)
    }

    fun getDate(): String {
        return getSystemDate()
    }
}