package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants.DATA_BASE.PRAYER_TABLE_NAME

data class PrayerScheduleConverter(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)
