package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss

class UserRepository(
    private val hane24Api: Hane24Api,
    private val sharedPreferenceUtilss: SharedPreferenceUtilss
) {
    suspend fun getInfo(token: String? = sharedPreferenceUtilss.getAccessToken()): MainInfo =
        hane24Api.getMainInfo(token)

    suspend fun getAccumulationTime(token: String? = sharedPreferenceUtilss.getAccessToken()): AccumulationTimeInfo =
        hane24Api.getAccumulationTime(token)
}