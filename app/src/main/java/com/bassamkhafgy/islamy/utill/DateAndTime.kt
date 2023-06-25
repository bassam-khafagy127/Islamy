package com.bassamkhafgy.islamy.utill

import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


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

fun calculateElapsedTimeCountDown(timeString: String): Long {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val targetTime = dateFormat.parse(timeString)
    val currentTime = dateFormat.parse(getCurrentTime())

    val currentTimeCalendar = Calendar.getInstance()
    val targetTimeCalendar = Calendar.getInstance()

    // Set the current time
    if (currentTime != null) {
        currentTimeCalendar.time = currentTime
    }

    // Set the target time
    if (targetTime != null) {
        targetTimeCalendar.time = targetTime
    }
    targetTimeCalendar.set(Calendar.YEAR, currentTimeCalendar.get(Calendar.YEAR))
    targetTimeCalendar.set(Calendar.MONTH, currentTimeCalendar.get(Calendar.MONTH))
    targetTimeCalendar.set(Calendar.DAY_OF_MONTH, currentTimeCalendar.get(Calendar.DAY_OF_MONTH))

    // Check if the target time is before the current time, indicating it's for the next day
    if (targetTimeCalendar.before(currentTimeCalendar)) {
        targetTimeCalendar.add(Calendar.DAY_OF_MONTH, 1) // Increment the day by 1
    }

    val durationMillis = targetTimeCalendar.timeInMillis - currentTimeCalendar.timeInMillis

    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60

    val elapsedMillis = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)
    return SystemClock.elapsedRealtime() + elapsedMillis
}



