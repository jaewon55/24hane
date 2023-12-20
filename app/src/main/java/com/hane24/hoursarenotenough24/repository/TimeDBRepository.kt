package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.data.AccumulationTimeWithTagLog
import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.database.AccumulationTimeDto
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TagLogDto
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import java.text.SimpleDateFormat
import java.util.Locale

class TimeDBRepository(private val db: TimeDatabase) {
    private suspend fun getTagLogPerMonth(year: Int, month: Int): List<TagLog> {
        val dbData = db.timeDatabaseDAO().getTagLogMonth(String.format("%04d%02d", year, month))
        return if (dbData.isEmpty() || isLastMonthOrThisMonth(dbData.first())) {
            emptyList()
        } else {
            dbData.map { TagLog(it.inTimeStamp, it.outTimeStamp, it.duration) }
        }
    }

    suspend fun getAccumulationTimeWithLogs(year: Int, month: Int): AccumulationTimeWithTagLog {
        val accumulationTime =
            db.timeDatabaseDAO().getAccumulationTime(String.format("%04d%02d", year, month))
        val tagLogs = getTagLogPerMonth(year, month)
        return AccumulationTimeWithTagLog(
            accumulationTime?.totalAccumulationTime ?: -1,
            accumulationTime?.acceptedAccumulationTime ?: -1,
            tagLogs
        )
    }

    suspend fun insert(logs: List<TagLogDto>) {
        db.timeDatabaseDAO().insertTagLogAll(*(logs.toTypedArray()))
    }

    suspend fun insert(logs: List<TagLogDto>, accumulationTime: AccumulationTimeDto) {
        db.timeDatabaseDAO().insertTagLogAll(*(logs.toTypedArray()))
        db.timeDatabaseDAO().insertAccumulationTime(accumulationTime)
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteTagLogAll()
        db.timeDatabaseDAO().deleteAccumulationTimeAll()
    }

    private fun isLastMonthOrThisMonth(data: TagLogDto): Boolean {
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