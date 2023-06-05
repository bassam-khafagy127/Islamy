package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants.DATABASE.PRAYER_TABLE_NAME

@Entity(tableName = PRAYER_TABLE_NAME)
data class PrayerSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val address: String
)
