package com.hane24.hoursarenotenough24.repository

import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.network.ReissueRequestResult
import com.hane24.hoursarenotenough24.network.ReissueState
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class ReissueRepository {
    suspend fun getState(token: String? = SharedPreferenceUtils.getAccessToken()): ReissueState =
        Hane24Apis.hane24ApiService.getReissueState(token)

    suspend fun reissue(token: String? = SharedPreferenceUtils.getAccessToken()): ReissueRequestResult =
        Hane24Apis.hane24ApiService.postReissueRequest(token)

    suspend fun finish(token: String? = SharedPreferenceUtils.getAccessToken()): ReissueRequestResult =
        Hane24Apis.hane24ApiService.patchReissueFinish(token)
}