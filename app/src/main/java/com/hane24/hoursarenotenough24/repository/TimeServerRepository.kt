package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.data.DomainModelDto
import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss

class TimeServerRepository(
    private val hane24Apis: Hane24Api,
    private val sharedPreferenceUtils: SharedPreferenceUtilss
) {
    suspend fun getTagLogPerMonth(
        year: Int,
        month: Int,
        token: String? = sharedPreferenceUtils.getAccessToken()
    ): List<TagLog> =
        hane24Apis.getAllTagPerMonth(
            token,
            year,
            month
        ).inOutLogs
}