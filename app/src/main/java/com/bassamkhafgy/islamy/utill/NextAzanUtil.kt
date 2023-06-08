package com.bassamkhafgy.islamy.utill

import android.util.Log
import com.bassamkhafgy.islamy.data.local.PrayerTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun getNextAzanTitle(prayerTimes: List<PrayerTime>, currentTime: String): PrayerTime {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val fajr: LocalTime = LocalTime.parse(prayerTimes[0].time, timeFormatter)
    val sunrise: LocalTime = LocalTime.parse(prayerTimes[1].time, timeFormatter)
    val dhuhr: LocalTime = LocalTime.parse(prayerTimes[2].time, timeFormatter)
    val asr: LocalTime = LocalTime.parse(prayerTimes[3].time, timeFormatter)
    val maghrib: LocalTime = LocalTime.parse(prayerTimes[4].time, timeFormatter)
    val isha: LocalTime = LocalTime.parse(prayerTimes[5].time, timeFormatter)

//    when {
//        dhuhr.isAfter(asr) -> {
//            Log.d("PrayeTime:", "$dhuhr dhuhr is after $asr asr")
//
//        }
//
//        dhuhr.isBefore(asr) -> {
//            Log.d("PrayeTime:", "$dhuhr dhuhr is before $asr asr")
//        }
//
//        dhuhr == asr -> {
//            Log.d("PrayeTime:", "Both is equal")
//        }
//    }
    return prayerTimes[4]
}