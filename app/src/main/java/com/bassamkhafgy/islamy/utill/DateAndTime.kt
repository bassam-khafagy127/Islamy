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
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val time = inputFormat.parse(time24)
    return outputFormat.format(time)
}
fun convertToApiDateFormat(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date)
}