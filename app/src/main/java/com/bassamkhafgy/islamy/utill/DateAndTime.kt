package com.bassamkhafgy.islamy.utill

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getSystemDate(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat =
        SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    return dateFormat.format(
        currentDate
    )
}
fun convertTo12HourFormat(time24: String): String {
    val inputFormat = SimpleDateFormat("HH:mm")
    val outputFormat = SimpleDateFormat("hh:mm a")
    val time = inputFormat.parse(time24)
    return outputFormat.format(time)
}