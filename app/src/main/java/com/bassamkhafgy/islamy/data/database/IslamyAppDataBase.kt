package com.bassamkhafgy.islamy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bassamkhafgy.islamy.data.local.PrayerSchedule

@Database(
    entities = [PrayerSchedule::class],
    version = 2,
    exportSchema = false
)
abstract class IslamyAppDataBase : RoomDatabase() {
    abstract fun timingsDao(): TimingsDao

}