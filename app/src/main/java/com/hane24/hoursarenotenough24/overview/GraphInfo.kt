package com.hane24.hoursarenotenough24.overview

import com.hane24.hoursarenotenough24.App
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GraphInfo(private val accumulationTimes: List<Long>, private val isMonth: Boolean) {
    private val percentTimes: List<Long> = parseTimeToPercent()

    fun calculateGraphHeight(
        index: Int,
        density: Float = App.instance.applicationContext.resources.displayMetrics.density
    ): Int {
        val value = percentTimes[index]
        val maxHeight = 87
        val whiteHeight = (maxHeight * (value * 0.01)).toInt() + 10 // maxHeight * percent + minHeight

        return whiteHeight
    }

    private fun parseTimeToPercent(): List<Long> {
        val maxItem = accumulationTimes.maxOf { it }

        return accumulationTimes.map{ (it.toDouble() / maxItem * 100L).toLong() }
    }

    fun calculateTotalTime(index: Int): Double {
        return accumulationTimes[index] / 3600.0
    }

    fun calculateAverageTime(index: Int): Double {
        val days = if (!isMonth) 7 else 30
        val timePerHour = accumulationTimes[index] / 3600.0

        return timePerHour / days
    }

    fun parseDateText(index: Int, calendar: Calendar): String {
        if (isMonth) {
            val format = SimpleDateFormat("yyyy.M", Locale("ko"))

            calendar.add(Calendar.MONTH, -index)
            return format.format(calendar.time)
        } else {
            val format = SimpleDateFormat("M.d(E)", Locale("ko"))
            calendar.add(Calendar.DATE, -(index*7))

            val (toDate, fromDate) = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> {
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.TUESDAY -> {
                    calendar.add(Calendar.DATE, -1)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.WEDNESDAY -> {
                    calendar.add(Calendar.DATE, -2)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.THURSDAY -> {
                    calendar.add(Calendar.DATE, -3)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.FRIDAY -> {
                    calendar.add(Calendar.DATE, -4)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.SATURDAY -> {
                    calendar.add(Calendar.DATE, -5)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                Calendar.SUNDAY -> {
                    calendar.add(Calendar.DATE, -6)
                    val toDate = format.format(calendar.time)
                    calendar.add(Calendar.DATE, 6)
                    val fromDate = format.format(calendar.time)
                    toDate to fromDate
                }
                else -> throw IllegalStateException("Exception")
            }
            return "$toDate - $fromDate"
        }
    }
}