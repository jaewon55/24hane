package com.hane24.hoursarenotenough24.data

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.network.InOutLog
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import com.hane24.hoursarenotenough24.utils.calculateDaysOfMonth
import java.text.SimpleDateFormat
import java.util.*


data class CalendarItem(
    val day: Int,
    val durationTime: Long,
    val isNextDay: Boolean,
) {
    val color
        get() = when {
            isNextDay -> R.color.log_list_background
            durationTime == 0L -> R.color.log_list_background
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

data class MonthTimeLogContainer(
    val year: Int,
    val month: Int,
    var logs: List<TimeLogItem>
)

data class TimeLogItem(
    val day: Int,
    val inTime: String,
    val outTime: String,
    val durationTime: Long?
)

fun MonthTimeLogContainer.getLogTableList(day: Int, inOutState: Boolean): List<LogTableItem> {
    return logs.filter { it.day == day }.map { log ->
        val durationString = log.durationTime?.let { time ->
            var second = time
            val hour = second / 3600
            second -= hour * 3600
            val min = second / 60
            second -= min * 60
            String.format("%02d:%02d:%02d", hour, min, second)
        } ?: run {
            if (inOutState && day == TodayCalendarUtils.day && logs.indexOf(log) == 0) {
                "-"
            } else {
                "누락"
            }
        }
        LogTableItem(log.inTime, log.outTime, durationString)
    }
}

fun MonthTimeLogContainer.getCalendarList(): List<CalendarItem> {
    val calendar = Calendar.getInstance().apply { set(year, month - 1, 1) }
    val newList = mutableListOf<CalendarItem>()
    for (i in -7..-1) {
        newList.add(CalendarItem(i, 0, false))
    }
    for (i in 1 until calendar.get(Calendar.DAY_OF_WEEK)) {
        newList.add(CalendarItem(0, 0, false))
    }
    for (i in 1..calendar.calculateDaysOfMonth()) {
        val durationTime = logs.filter { it.day == i }.sumOf { it.durationTime ?: 0 }
        val isNextDay = when {
            calendar.get(Calendar.YEAR) < TodayCalendarUtils.year -> false
            calendar.get(Calendar.MONTH) + 1 < TodayCalendarUtils.month -> false
            i > TodayCalendarUtils.day -> true
            else -> false
        }
        newList.add(CalendarItem(i, durationTime, isNextDay))
    }
    return newList
}

data class DomainModelDto(
    val year: Int,
    val month: Int,
    val tagLogs: List<TagLog>
)

data class TagLog(
    val inTimeStamp: Long?,
    val outTimeStamp: Long?,
    val durationSecond: Long?
)

fun List<TagLog>.asDatabaseDto(year: Int, month: Int): List<TimeDatabaseDto> {
    val date = String.format("%4d%02d", year, month)
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
    return if (isEmpty()) {
        listOf(
            TimeDatabaseDto(
                date + "00", 0, 0, 0, System.currentTimeMillis()
            )
        )
    } else {
        map { log ->
            val dateOfLog = when {
                log.inTimeStamp != null -> simpleDateFormat.format(log.inTimeStamp * 1000)
                log.outTimeStamp != null -> simpleDateFormat.format(log.outTimeStamp * 1000)
                else -> date + "00"
            }
            TimeDatabaseDto(
                dateOfLog,
                log.inTimeStamp ?: 0,
                log.outTimeStamp ?: 0,
                log.durationSecond,
                System.currentTimeMillis()
            )
        }
    }
}
