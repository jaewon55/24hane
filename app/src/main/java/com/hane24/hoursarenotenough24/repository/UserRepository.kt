package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class UserRepository {
    suspend fun getInfo(token: String? = SharedPreferenceUtils.getAccessToken()): MainInfo =
        Hane24Apis.hane24ApiService.getMainInfo(token)
}