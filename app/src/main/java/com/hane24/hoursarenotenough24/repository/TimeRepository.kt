package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.InOutTimeContainer
import com.hane24.hoursarenotenough24.network.asDatabaseDto
import com.hane24.hoursarenotenough24.network.asDomainModel
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import java.text.SimpleDateFormat
import java.util.*

class TimeRepository(private val db: TimeDatabase) {

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

    fun getAll(): List<TimeDatabaseDto> {
        return db.timeDatabaseDAO().getAll()
    }

    suspend fun getMonth(date: String): List<TimeDatabaseDto> {
        val dataBaseData = db.timeDatabaseDAO().getMonth(date)
        return if (dataBaseData.isEmpty() || shouldReloadDataFromServer(dataBaseData[0])) {
            val year = date.substring(0, 4).toInt()
            val month = date.substring(4, 6).toInt()
            val monthTimeLog = Hane42Apis
                .hane42ApiService
                .getInOutInfoPerMonth(accessToken, year, month)
                .asDatabaseDto(date)
            insert(monthTimeLog)
            monthTimeLog
        } else {
            dataBaseData
        }
    }

    suspend fun getMonthNoneUpdate(date: String): List<TimeDatabaseDto> {
        return db.timeDatabaseDAO().getMonth(date)
    }

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