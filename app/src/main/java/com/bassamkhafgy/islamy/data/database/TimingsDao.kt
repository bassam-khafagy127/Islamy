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

    @Update
    suspend fun updateTimings(time: PrayerSchedule)


    @Query("SELECT*FROM `PRAYER TIMES` ORDER BY id DESC LIMIT 1")
    fun getDayTimings(): PrayerSchedule

    @Query("SELECT COUNT(*) FROM `PRAYER TIMES`")
    fun isTableEmpty(): Int
    @Query("DELETE FROM `PRAYER TIMES`")
    suspend fun deleteOldData()

}