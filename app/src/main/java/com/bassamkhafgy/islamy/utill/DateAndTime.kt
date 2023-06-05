package com.bassamkhafgy.islamy.utill

import android.text.format.Time
import android.util.Log
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.PrayerScheduleConverter
import com.bassamkhafgy.islamy.data.local.utillmodels.PrayerTime
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private var _remainingTimeLiveData = MutableStateFlow("")

fun getSystemDate(): String {
    val currentDate = Calendar.getInstance().time
    val dateFormat =
        SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    return dateFormat.format(
        currentDate
    )
}

fun getSystemCurrentTime(): String {
    val currentTime = Calendar.getInstance()
    val timeFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
    return timeFormat.format(currentTime.time)
}

fun convertTo12HourFormat(time24: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
    val time = inputFormat.parse(time24)
    return outputFormat.format(time!!)
}

fun convertToApiDateFormat(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date!!)
}
fun convertResponseDateFormat(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date!!)
}


fun getPrayerRemainingTime(
    currentTime: String,
    prayerTime: String
): Long {
    val currentTimeObj = Time()
    val prayerTimeObj = Time()
    // Set current time
    val currentTimeSplit = currentTime.split(":")
    currentTimeObj.hour = currentTimeSplit[0].toInt()
    currentTimeObj.minute = currentTimeSplit[1].toInt()

    // Set prayer time
    val prayerTimeSplit = prayerTime.split(":")
    prayerTimeObj.hour = prayerTimeSplit[0].toInt()
    prayerTimeObj.minute = prayerTimeSplit[1].toInt()

    // If the prayer time has already passed for the day, set it to the next day
    if (prayerTimeObj.before(currentTimeObj)) {
        prayerTimeObj.set(currentTimeObj)
        prayerTimeObj.minute += 1
    }


    return prayerTimeObj.toMillis(true) - currentTimeObj.toMillis(true)
}

fun calculateNextAzanTime(prayerTimes: PrayerScheduleConverter): Pair<String, String> {
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val azanTimes = listOf(
        "Fajr" to prayerTimes.fajr,
        "Sunrise" to prayerTimes.sunrise,
        "Dhuhr" to prayerTimes.dhuhr,
        "Asr" to prayerTimes.asr,
        "Maghrib" to prayerTimes.maghrib,
        "Isha" to prayerTimes.isha
    )

    var nextAzanTime = ""
    var nextAzanTitle = ""
    var minTimeDifference = Int.MAX_VALUE

    for (azanTime in azanTimes) {
        val timeDifference = calculateMinuteDifference(currentTime, azanTime.second)
        if (timeDifference in 1 until minTimeDifference) {
            minTimeDifference = timeDifference
            nextAzanTime = azanTime.second
            nextAzanTitle = azanTime.first
        }
    }

    return nextAzanTitle to nextAzanTime
}

private fun calculateMinuteDifference(time1: String, time2: String): Int {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date1 = timeFormat.parse(time1)
    val date2 = timeFormat.parse(time2)

    val differenceInMillis = date2.time - date1.time
    return (differenceInMillis / (1000 * 60)).toInt()
}
