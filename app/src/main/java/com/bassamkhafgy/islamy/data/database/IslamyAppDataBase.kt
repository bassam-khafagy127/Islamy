package com.bassamkhafgy.islamy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.StoringAddress

@Database(
    entities = [
        PrayerSchedule::class,
        StoringAddress::class],
    version = 3,
    exportSchema = false
)
abstract class IslamyAppDataBase : RoomDatabase() {
    abstract fun timingsDao(): TimingsDao
    abstract fun addressDao(): AddressDao

}