package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import java.text.SimpleDateFormat
import java.util.Locale

class TimeDBRepository(private val db: TimeDatabase) {
    suspend fun getTagLogPerMonth(year: Int, month: Int): List<TagLog> {
        val dbData = db.timeDatabaseDAO().getMonth(String.format("%04d%02d", year, month))
        return if (dbData.isEmpty() || isLastMonthOrThisMonth(dbData.first())) {
            emptyList()
        } else {
            dbData.map { TagLog(it.inTimeStamp, it.outTimeStamp, it.duration) }
        }
    }

    suspend fun insert(logs: List<TimeDatabaseDto>) {
        db.timeDatabaseDAO().insertAll(*(logs.toTypedArray()))
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteAll()
    }

    private fun isLastMonthOrThisMonth(data: TimeDatabaseDto): Boolean {
        val updateTime = SimpleDateFormat("yyyyMM", Locale("ko")).format(data.updateTime)
        val dataDate = data.date.substring(0, 6)
        var y = TodayCalendarUtils.year
        val m = TodayCalendarUtils.month.minus(1).let {
            if (it == 0) {
                --y
                12
            } else {
                it
            }
        }
        val lastMonth = String.format("%04d%02d", y, m)
        return updateTime == dataDate || lastMonth == dataDate
    }
}