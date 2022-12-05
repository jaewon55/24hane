package com.hane24.hoursarenotenough24.data

import android.view.View
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.utils.calculateDaysOfMonth
import java.util.*


data class CalendarItem(
    val day: Int,
    val durationTime: Long
) {
    val dayString: String
        get() = day.toString()
    val color: Int
        get() = when {
            durationTime == 0L -> R.color.white
            durationTime <= 3L * 3600 -> R.color.calendar_color1
            durationTime <= 6L * 3600 -> R.color.calendar_color2
            durationTime <= 9L * 3600 -> R.color.calendar_color3
            else -> R.color.calendar_color4
        }
}

data class LogTableItem(
    val inTime: String,
    val outTime: String,
    val durationTime: String
)

data class MonthTimeLogContainer(val monthLog: List<TimeLogItem>)

data class TimeLogItem(
    val day: Int,
    val inTime: String,
    val outTime: String,
    val durationTime: Long
)

fun MonthTimeLogContainer.getLogTableList(day: Int): List<LogTableItem> {
    return monthLog.filter { it.day == day }.map { log ->
        val durationString = log.durationTime.let { time ->
            var second = time
            val hour = second / 3600
            second -= hour * 3600
            val min = second / 60
            second -= min * 60
            String.format("%02d:%02d:%02d", hour, min, second)
        }
        LogTableItem(log.inTime, log.outTime, durationString)
    }
}

fun MonthTimeLogContainer.getCalendarList(calendar: Calendar): List<CalendarItem> {
    val newList = mutableListOf<CalendarItem>()
    for (i in 1 until calendar.get(Calendar.DAY_OF_WEEK)) {
        newList.add(CalendarItem(0, 0))
    }
    for (i in 1..calendar.calculateDaysOfMonth()) {
        val durationTime = monthLog.filter { it.day == i }.sumOf { it.durationTime }
        newList.add(CalendarItem(i, durationTime))
    }
    return newList
}
