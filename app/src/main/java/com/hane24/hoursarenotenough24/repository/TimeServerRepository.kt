package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.network.InOutTimeItem

class TimeServerRepository : TimeRepository<InOutTimeItem> {

    override suspend fun getTimeByMonth(year: Int, month: Int, token: String?): List<InOutTimeItem> {
        return Hane24Apis.hane24ApiService.getAllTagPerMonth(
            token,
            year,
            month
        ).inOutLogs
    }
}