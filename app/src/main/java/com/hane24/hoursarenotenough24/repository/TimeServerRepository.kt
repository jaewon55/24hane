package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.data.AccumulationTimeWithTagLog
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class TimeServerRepository(
    private val hane24Apis: Hane24Api,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    suspend fun getTagLogPerMonth(
        year: Int,
        month: Int,
        token: String? = sharedPreferenceUtils.getAccessToken()
    ): AccumulationTimeWithTagLog {
        val allTagPerMonthDto = hane24Apis.getAllTagPerMonth(token, year, month)
        return allTagPerMonthDto.let {
            AccumulationTimeWithTagLog(
                it.totalAccumulationTime,
                it.acceptedAccumulationTime,
                it.inOutLogs
            )
        }
    }
}