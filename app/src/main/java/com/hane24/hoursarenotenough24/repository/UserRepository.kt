package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class UserRepository(
    private val hane24Api: Hane24Api,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    suspend fun getInfo(token: String? = sharedPreferenceUtils.getAccessToken()): MainInfo =
        hane24Api.getMainInfo(token)

    suspend fun getAccumulationTime(token: String? = sharedPreferenceUtils.getAccessToken()): AccumulationTimeInfo =
        hane24Api.getAccumulationTime(token)
}