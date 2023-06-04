package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.TimeStore

@Dao
interface LocationDao {
    @Insert
    fun insertLocation(lastLocation: LastLocation)

    @Update
    fun updateLocation(lastLocation: LastLocation)

    @Query("SELECT * FROM `last location`")
    fun getLastLocation(): List<LastLocation>
}