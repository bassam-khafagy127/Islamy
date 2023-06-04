package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants.DATA_BASE.PRAYER_TABLE_NAME

@Entity(tableName = PRAYER_TABLE_NAME)
data class TimeStore(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fagr: String,
    val sunrise: String,
    val duhr: String,
    val asr: String,
    val magribe: String,
    val isha: String
)
