package com.bassamkhafgy.islamy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.StoringAddress

@Dao
interface AddressDao {
    @Insert
    suspend fun insertAddress(address: StoringAddress)

    @Query("SELECT*FROM `ADDRESS LOCATION` ORDER BY id DESC LIMIT 1")
    fun getAddress(): StoringAddress

    @Query("DELETE FROM `ADDRESS LOCATION`")
    suspend fun deleteOldData()


}