package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils


interface TimeRepository {
    /* server, DB 다름 */
    suspend fun getTagLogPerMonth(
        year: Int,
        month: Int,
        token: String?
    ): List<TagLog>

    suspend fun getAccumulationTime(token: String?): AccumulationTimeInfo =
        Hane24Apis.hane24ApiService.getAccumulationTime(token)
}