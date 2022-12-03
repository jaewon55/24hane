package com.hane24.hoursarenotenough24.utils

import java.util.*


object TodayCalendarUtils {
    private var calendar = Calendar.getInstance()
    val year: Int
        get() {
            calendar = Calendar.getInstance()
            return calendar.get(Calendar.YEAR)
        }
    val month: Int
        get() {
            calendar = Calendar.getInstance()
            return calendar.get(Calendar.MONTH) + 1
        }
    val day: Int
        get() {
            calendar = Calendar.getInstance()
            return calendar.get(Calendar.DAY_OF_MONTH)
        }
}