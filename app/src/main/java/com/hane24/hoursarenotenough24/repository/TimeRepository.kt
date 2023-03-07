package com.hane24.hoursarenotenough24.repository

import android.util.Log
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.asDatabaseDto
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TimeRepository(private val db: TimeDatabase) {

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

    fun getAll(): List<TimeDatabaseDto> {
        return db.timeDatabaseDAO().getAll()
    }

    suspend fun getMonthOrNull(date: String): List<TimeDatabaseDto>? {
        val dataBaseData = db.timeDatabaseDAO().getMonth(date)
        return if (dataBaseData.isEmpty() || shouldReloadDataFromServer(dataBaseData[0])) {
            null
        } else {
            dataBaseData
        }
    }

    suspend fun getMonthFromServer(date: String): List<TimeDatabaseDto> {
        Log.d("accessToken", accessToken.toString())
        val year = date.substring(0, 4).toInt()
        val month = date.substring(4, 6).toInt()
        val networkData = Hane42Apis
            .hane42ApiService
            .getAllTagPerMonth(accessToken, year, month)
        val monthTimeLog = networkData.asDatabaseDto(date)
        withContext(Dispatchers.IO) {
            Log.d("logList", "$date == ${String.format("%4d%02d", TodayCalendarUtils.year, TodayCalendarUtils.month)}")
            if (date == String.format("%4d%02d", TodayCalendarUtils.year, TodayCalendarUtils.month)) {
                val dbData = getDay("$date${String.format("%02d", TodayCalendarUtils.day)}")
                Log.d("logList", "todayList : $dbData")
                Log.d("logList", "monthFirst ${monthTimeLog[0].inTimeStamp}")
                dbData.forEach {
                    if (it.inTimeStamp == monthTimeLog[0].inTimeStamp) {
                        deleteOne(it.date, it.inTimeStamp, it.outTimeStamp)
                    }
                }
            }
            insert(monthTimeLog)
        }
        Log.d("logList", "getMonth : $monthTimeLog")
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

    suspend fun deleteOne(date: String, inTime: Long, outTime: Long) {
        db.timeDatabaseDAO().deleteOne(date, inTime, outTime)
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteAll()
    }

    private fun shouldReloadDataFromServer(data: TimeDatabaseDto): Boolean {
        val updateTime = SimpleDateFormat("yyyyMM", Locale("ko")).format(data.updateTime)
        return updateTime == data.date.substring(0, 6)
    }

}