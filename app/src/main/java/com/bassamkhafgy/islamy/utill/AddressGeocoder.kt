package com.bassamkhafgy.islamy.utill

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LAT
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LONG
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
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

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("MissingPermission")
suspend fun getLocationLatitudeLongitude(fusedLocationProviderClient: FusedLocationProviderClient): MutableSharedFlow<Resource<Location>> {

    val liveLocation: MutableSharedFlow<Resource<Location>> = MutableSharedFlow()
    val location = fusedLocationProviderClient.lastLocation

    GlobalScope.launch {
        liveLocation.emit(Resource.Loading())
    }
    location.addOnSuccessListener {
        GlobalScope.launch(Dispatchers.Unconfined) {
            liveLocation.emit(Resource.Success(it))
        }
    }.addOnFailureListener {
        GlobalScope.launch(Dispatchers.Unconfined) {
            liveLocation.emit(Resource.Error(it.localizedMessage))
        }
        Log.e(Constants.ERROR_TAG + "ADDRESS_GEOCODER LAT LONG", it.message.toString())
    }
    return liveLocation

}