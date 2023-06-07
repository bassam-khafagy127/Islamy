package com.bassamkhafgy.islamy.utill

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




