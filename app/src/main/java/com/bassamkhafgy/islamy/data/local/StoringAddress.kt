package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants


@Entity(tableName = Constants.DATABASE.ADDRESS_TABLE_NAME)
data class StoringAddress(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val address: String,
    val latitude: String,
    val longitude: String
)