package com.bassamkhafgy.islamy.utill

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun getSystemDate(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat =
        SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    return dateFormat.format(
        currentDate
    )
}

fun convertDateFormat(dateString: String): String {
    val inputFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())

    val date = inputFormat.parse(dateString)
    return outputFormat.format(date)
}

//convert Api To response time 12hrs
fun getTime12hrsFormat(time24: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val time = inputFormat.parse(time24)
    return outputFormat.format(time!!)
}

//get date for api request
fun getTimeForApi(): String {
    return SimpleDateFormat("d-M-yyyy", Locale.ENGLISH).format(Date())
}

fun getDayCounter(daysToAdd: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val formattedDate = dateFormat.format(calendar.time)
    Log.d("NEXT DAY", formattedDate)

    return formattedDate
}

fun getCurrentTime(): String {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val currentTime = Calendar.getInstance().time
    return timeFormat.format(currentTime)
}



