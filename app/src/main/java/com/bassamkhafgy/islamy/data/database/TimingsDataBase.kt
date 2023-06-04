package com.bassamkhafgy.islamy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeStore

@Database(entities = [TimeStore::class, LastLocation::class], version = 1)
abstract class TimingsDataBase : RoomDatabase() {
    abstract fun timingsDao(): TimingsDao
    abstract fun locationDao(): LocationDao
}