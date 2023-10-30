package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils


interface TimeRepository<T> {
    /* server, DB 다름 */
    suspend fun getTimeByMonth(
        year: Int,
        month: Int,
        token: String? = SharedPreferenceUtils.getAccessToken()
    ): List<T>

    suspend fun getAccumulationTime(token: String?): AccumulationTimeInfo =
        Hane24Apis.hane24ApiService.getAccumulationTime(token)
}