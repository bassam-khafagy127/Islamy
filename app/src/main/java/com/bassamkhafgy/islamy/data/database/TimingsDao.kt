package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.PrayerSchedule

@Dao
interface TimingsDao {
    @Insert
    suspend fun insertTimings(time: PrayerSchedule)

    @Query("SELECT*FROM `PRAYER TIMES` ORDER BY id DESC LIMIT 1")
    fun getDayTimings(): PrayerSchedule

    @Query("UPDATE `PRAYER TIMES` SET address = :newAddress WHERE address IS NULL")
    fun updateAddress(newAddress: String)

    @Query("DELETE FROM `PRAYER TIMES`")
    suspend fun deleteOldData()


}