package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeStore

@Dao
interface TimingsDao {
    @Insert
    fun insertTimings(time: TimeStore)

    @Update
    fun updateTimings(time: TimeStore)

    @Query("SELECT*FROM `PRAYER TIMES`")
    fun getAllTimings(): List<TimeStore>

    @Query("SELECT COUNT(*) FROM `PRAYER TIMES`")
    fun isTableEmpty(): Int
}