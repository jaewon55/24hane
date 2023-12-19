package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TagLogDto
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

    suspend fun getMonthOrNull(date: String): List<TagLogDto>? {
        val dataBaseData = db.timeDatabaseDAO().getTagLogMonth(date)
        return if (dataBaseData.isEmpty() || shouldReloadDataFromServer(dataBaseData[0])) {
            null
        } else {
            dataBaseData
        }
    }

    suspend fun getMonthFromServer(date: String): List<TagLogDto> {
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

    suspend fun getMonthNoneUpdate(date: String): List<TagLogDto> =
        db.timeDatabaseDAO().getTagLogMonth(date)

    private suspend fun insert(timeInfo: List<TagLogDto>) {
        db.timeDatabaseDAO().insertTagLogAll(*(timeInfo.toTypedArray()))
    }

    suspend fun deleteMonth(date: String) {
//        db.timeDatabaseDAO().deleteTagLogMonth(date)
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteTagLogAll()
    }

    private fun shouldReloadDataFromServer(data: TagLogDto): Boolean {
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