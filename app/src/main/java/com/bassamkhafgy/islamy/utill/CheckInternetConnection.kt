package com.bassamkhafgy.islamy.utill

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun isInternetConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // For devices running Android Q and above
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    }

    // For devices running below Android Q
    else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        activeNetworkInfo?.isConnectedOrConnecting ?: false
    }
}