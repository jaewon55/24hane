package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.network.asDatabaseDto
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TimeRepositoryC(private val db: TimeDatabase) {

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

//    fun getAll(): List<TimeDatabaseDto> {
//        return db.timeDatabaseDAO().getAll()
//    }

    suspend fun getMonthOrNull(date: String): List<TimeDatabaseDto>? {
        val dataBaseData = db.timeDatabaseDAO().getMonth(date)
        return if (dataBaseData.isEmpty() || shouldReloadDataFromServer(dataBaseData[0])) {
            null
        } else {
            dataBaseData
        }
    }

    suspend fun getMonthFromServer(date: String): List<TimeDatabaseDto> {
        val year = date.substring(0, 4).toInt()
        val month = date.substring(4, 6).toInt()
        val networkData = Hane24Apis
            .hane24ApiService
            .getAllTagPerMonth(accessToken, year, month)
        val monthTimeLog = networkData.asDatabaseDto(date)
        withContext(Dispatchers.IO) {
            deleteMonth(date)
            insert(monthTimeLog)
        }
        return monthTimeLog
    }

    suspend fun getMonthNoneUpdate(date: String): List<TimeDatabaseDto> =
        db.timeDatabaseDAO().getMonth(date)

    suspend fun getDay(date: String): List<TimeDatabaseDto> {
        return db.timeDatabaseDAO().getDay(date)
    }

    private suspend fun insert(timeInfo: List<TimeDatabaseDto>) {
        db.timeDatabaseDAO().insertAll(*(timeInfo.toTypedArray()))
    }

    suspend fun deleteMonth(date: String) {
        db.timeDatabaseDAO().deleteMonth(date)
    }

    suspend fun deleteOne(date: String, inTime: Long, outTime: Long) {
        db.timeDatabaseDAO().deleteOne(date, inTime, outTime)
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteAll()
    }

    private fun shouldReloadDataFromServer(data: TimeDatabaseDto): Boolean {
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