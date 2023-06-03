package com.bassamkhafgy.islamy.utill

import android.os.CountDownTimer
import android.text.format.Time
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val _remainingTimeLiveData = MutableStateFlow("")

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
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return timeFormat.format(currentTime.time)
}

fun convertTo12HourFormat(time24: String): String {
    val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val time = inputFormat.parse(time24)
    return outputFormat.format(time!!)
}

fun convertToApiDateFormat(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy, MMMM d", Locale.getDefault())
    val outputFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val date = inputFormat.parse(inputDate)
    return outputFormat.format(date!!)
}

var remainingTime = ""
fun getPrayerRemainingTime(currentTime: String, prayerTime: String): MutableStateFlow<String> {
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

    val durationMillis = prayerTimeObj.toMillis(true) - currentTimeObj.toMillis(true)

    val countDownTimer = object : CountDownTimer(durationMillis, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000 % 60
            val minutes = millisUntilFinished / (1000 * 60) % 60
            val hours = millisUntilFinished / (1000 * 60 * 60)

            remainingTime = "$hours:$minutes\n$seconds "
            runBlocking {
                _remainingTimeLiveData.emit(remainingTime)
            }
        }

        override fun onFinish() {
            remainingTime = "0:0"
        }

    }

    countDownTimer.start()
    return _remainingTimeLiveData
}
