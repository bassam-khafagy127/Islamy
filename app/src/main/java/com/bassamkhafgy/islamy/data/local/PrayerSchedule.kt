package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants.DATABASE.PRAYER_TABLE_NAME

@Entity(tableName = PRAYER_TABLE_NAME)
data class PrayerSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var fajr: String,
    var sunrise: String,
    var dhuhr: String,
    var asr: String,
    var maghrib: String,
    var isha: String,
)
