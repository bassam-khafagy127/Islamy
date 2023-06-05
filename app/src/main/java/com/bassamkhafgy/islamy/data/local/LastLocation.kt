package com.bassamkhafgy.islamy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bassamkhafgy.islamy.utill.Constants

@Entity(tableName = Constants.DATABASE.LOCATION_TABLE_NAME)
data class LastLocation(
    @PrimaryKey(autoGenerate = true)
     val id: Int,
     val location: String
)
