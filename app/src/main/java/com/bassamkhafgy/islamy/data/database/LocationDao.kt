package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.LastLocation

@Dao
interface LocationDao {
    @Insert
    suspend fun insertAddress(lastLocation: LastLocation)

    @Update
    suspend fun updateAddress(lastLocation: LastLocation)

    @Query("SELECT * FROM `last location`")
    fun getLastAddress(): List<LastLocation>

    @Query("SELECT COUNT(*) FROM `last location`")
    fun isTableEmpty(): Int
}
