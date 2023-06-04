package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.TimeSchem

@Dao
interface TimingsDao {
    @Insert
    suspend fun insertTimings(time: TimeSchem)

    @Update
   suspend fun updateTimings(time: TimeSchem)

    @Query("SELECT*FROM `PRAYER TIMES`")
    fun getAllTimings(): List<TimeSchem>

    @Query("SELECT COUNT(*) FROM `PRAYER TIMES`")
    fun isTableEmpty(): Int
}