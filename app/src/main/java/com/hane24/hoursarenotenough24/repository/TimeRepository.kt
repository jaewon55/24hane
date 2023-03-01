package com.hane24.hoursarenotenough24.repository

import android.util.Log
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.asDatabaseDto
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
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
        withContext(Dispatchers.IO) { insert(monthTimeLog) }
        return monthTimeLog
    }

    suspend fun getMonthNoneUpdate(date: String): List<TimeDatabaseDto> = db.timeDatabaseDAO().getMonth(date)

    suspend fun getDay(date: String): List<TimeDatabaseDto> {
        return db.timeDatabaseDAO().getDay(date)
    }

    private suspend fun insert(timeInfo: List<TimeDatabaseDto>) {
        db.timeDatabaseDAO().insertAll(*(timeInfo.toTypedArray()))
    }

    suspend fun deleteAll() {
        db.timeDatabaseDAO().deleteAll()
    }

    private fun shouldReloadDataFromServer(data: TimeDatabaseDto): Boolean {
        val updateTime = SimpleDateFormat("yyyyMM", Locale("ko")).format(data.updateTime)
        return updateTime == data.date.substring(0, 6)
    }

}