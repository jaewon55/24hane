package com.hane24.hoursarenotenough24.reissue

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.ReissueRequestResult
import com.hane24.hoursarenotenough24.network.ReissueState
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ReissueViewModel: ViewModel() {
    private val accessToken = SharedPreferenceUtils.getAccessToken()

    private val _reissueState: MutableLiveData<ReissueState?> = MutableLiveData(null)
    val reissueState: LiveData<ReissueState?>
        get() = _reissueState

    private val _reissueResult: MutableLiveData<ReissueRequestResult?> = MutableLiveData(null)
    val reissueResult: LiveData<ReissueRequestResult?>
        get() = _reissueResult

    init {
        viewModelScope.launch {
            useGetReissueStateApi()
        }
    }

    fun clickReissueButton(activity: FragmentActivity) {
        ReissueWarningDialog.showReissueDialog(activity.supportFragmentManager)
    }
    fun clickReissueOkButton() {
        viewModelScope.launch {
            usePostReissueApi()
        }
    }

    fun refreshButtonOnClick() {
        viewModelScope.launch {
            useGetReissueStateApi()
        }
    }

    private suspend fun useGetReissueStateApi() {
        try {
            _reissueState.value = Hane42Apis.hane42ApiService.getReissueState(accessToken)
        } catch(err: HttpException) {
            when(err.code()) {
                404 -> {_reissueState.value = null}
//                503 ->
            }

        } catch (e: Exception) {

        }
    }

    private suspend fun usePostReissueApi() {
        try {
            _reissueResult.value = Hane42Apis.hane42ApiService.postReissueRequest(accessToken)
        } catch(err: HttpException) {
//                404 ->
//                503 ->
        } catch (e: Exception) {

        }
    }
    private suspend fun usePatchReissueFinish() {
        try {
            _reissueResult.value = Hane42Apis.hane42ApiService.postReissueRequest(accessToken)
        } catch (err: HttpException) {
//                404 ->
//                503 ->
        } catch (e: Exception) {

        }
    }

}