package com.bassamkhafgy.islamy.data.local

data class PrayerScheduleConverter(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)
