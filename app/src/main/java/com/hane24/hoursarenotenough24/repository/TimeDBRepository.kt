package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto

class TimeDBRepository(private val db: TimeDatabase) : TimeRepository<TimeDatabaseDto> {
    override suspend fun getTimeByMonth(year: Int, month: Int, token: String?): List<TimeDatabaseDto> =
        db.timeDatabaseDAO().getMonth(String.format("%04d%02d", year, month))
}