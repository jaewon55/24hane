package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.Hane24Api
import com.hane24.hoursarenotenough24.network.ReissueRequestResult
import com.hane24.hoursarenotenough24.network.ReissueState
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class ReissueRepository(
    private val hane24Api: Hane24Api,
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    suspend fun getState(token: String? = sharedPreferenceUtils.getAccessToken()): ReissueState =
        hane24Api.getReissueState(token)

    suspend fun reissue(token: String? = sharedPreferenceUtils.getAccessToken()): ReissueRequestResult =
        hane24Api.postReissueRequest(token)

    suspend fun finish(token: String? = sharedPreferenceUtils.getAccessToken()): ReissueRequestResult =
        hane24Api.patchReissueFinish(token)
}