package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.network.ReissueRequestResult
import com.hane24.hoursarenotenough24.network.ReissueState
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss

class ReissueRepository(
    private val hane24Api: Hane24Api,
    private val sharedPreferenceUtilss: SharedPreferenceUtilss
) {
    suspend fun getState(token: String? = sharedPreferenceUtilss.getAccessToken()): ReissueState =
        hane24Api.getReissueState(token)

    suspend fun reissue(token: String? = sharedPreferenceUtilss.getAccessToken()): ReissueRequestResult =
        hane24Api.postReissueRequest(token)

    suspend fun finish(token: String? = sharedPreferenceUtilss.getAccessToken()): ReissueRequestResult =
        hane24Api.patchReissueFinish(token)
}