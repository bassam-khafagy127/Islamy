package com.bassamkhafgy.islamy.utill

import android.util.Log
import com.bassamkhafgy.islamy.data.local.PrayerTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getNextAzanTitle(prayerTimes: List<PrayerTime>): PrayerTime? {

    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val currentTime = dateFormat.format(Date()) // Current time in AM/PM format
    Log.d("PrayeTime: nextAzan", "Time:$currentTime")


    return null
}