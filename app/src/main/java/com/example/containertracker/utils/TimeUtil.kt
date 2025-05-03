package com.example.containertracker.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object TimeUtil {
    const val TIME_PATTERN = "HH:mm"
    @SuppressLint("SimpleDateFormat")
    fun formatLocalDateToString(date: Date): String {
        return SimpleDateFormat(TIME_PATTERN).format(date)
    }
}