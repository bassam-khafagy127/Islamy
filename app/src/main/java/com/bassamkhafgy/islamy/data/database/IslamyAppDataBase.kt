package com.bassamkhafgy.islamy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.PrayerSchedule

@Database(entities = [PrayerSchedule::class, LastLocation::class], version = 1)
abstract class IslamyAppDataBase : RoomDatabase() {
    abstract fun timingsDao(): TimingsDao
    abstract fun locationDao(): LocationDao
}