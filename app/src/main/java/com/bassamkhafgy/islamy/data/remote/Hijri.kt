package com.bassamkhafgy.islamy.data.remote


import com.google.gson.annotations.SerializedName

data class Hijri(
    @SerializedName("date")
    val date: String?,
    @SerializedName("day")
    val day: String?,
    @SerializedName("designation")
    val designation: Designation?,
    @SerializedName("format")
    val format: String?,
    @SerializedName("holidays")
    val holidays: List<Any>?,
    @SerializedName("month")
    val month: MonthX?,
    @SerializedName("weekday")
    val weekday: WeekdayX?,
    @SerializedName("year")
    val year: String?
)