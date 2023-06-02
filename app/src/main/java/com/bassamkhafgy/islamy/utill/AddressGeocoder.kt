package com.bassamkhafgy.islamy.utill

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.Locale

fun getAddressGeocoder(context: Context, latitude: Double, longitude: Double): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    val address: Address?

    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
    if (addresses != null) {
        address = addresses[0]
        //   val address = "$add, $state, $country"

        val city = address?.locality
        val state = address?.adminArea
        val country = address?.countryName
        return "$city, $state, $country"
    }
    return null
}