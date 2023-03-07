package com.hane24.hoursarenotenough24.reissue

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.ReissueRequestResult
import com.hane24.hoursarenotenough24.network.ReissueState
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ReissueViewModel : ViewModel() {
    private val accessToken = SharedPreferenceUtils.getAccessToken()

    private val _reissueState: MutableLiveData<ReissueState?> = MutableLiveData(null)
    val reissueState: LiveData<ReissueState?>
        get() = _reissueState

    private val _reissueResult: MutableLiveData<ReissueRequestResult?> = MutableLiveData(null)
    val reissueResult: LiveData<ReissueRequestResult?>
        get() = _reissueResult

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _errorState = MutableLiveData<State>()
    val errorState: LiveData<State>
        get() = _errorState

    init {
        viewModelScope.launch {
            _loadingState.value = true
            useGetReissueStateApi()
            _loadingState.value = false
        }
    }

    fun clickReissueButton(activity: FragmentActivity) {
        ReissueWarningDialog.showReissueDialog(activity.supportFragmentManager)
    }

    fun clickReissueOkButton() {
        viewModelScope.launch {
            _loadingState.value = true
            _reissueState.value?.let {
                when (it.state) {
                    "none", "done" -> usePostReissueApi()
                    "pick_up_requested" -> usePatchReissueFinish()
                }
            }
            _loadingState.value = false
        }
    }

    fun refreshButtonOnClick() {
        viewModelScope.launch {
            _loadingState.value = true
            useGetReissueStateApi()
            _loadingState.value = false
            Log.i("state", "state: ${reissueState.value}")
        }
    }

    private val arr = arrayOf(
        ReissueState("none"),
        ReissueState("apply"),
        ReissueState("in_progress"),
        ReissueState("pick_up_requested"),
        ReissueState("done")
    )

    private var idx = 0
        set(value) {
            field = if (value == 5) 0 else value
        }

    fun testStateChange() {
        Log.d("reissueState", "state : ${arr[idx].state}")
        _reissueState.value = arr[idx++]
    }

    private suspend fun useGetReissueStateApi() {
        try {
            _reissueState.value = Hane42Apis.hane42ApiService.getReissueState(accessToken)
            Log.d("issue", reissueState.value?.state ?: "몰라")
        } catch (err: HttpException) {
            when (err.code()) {
                404 -> {
                    _reissueState.value = null
                }
                503 -> {
                    _errorState.value = State.SERVER_FAIL
                }
            }
        } catch (e: Exception) {
            _errorState.value = State.NETWORK_FAIL
        } finally {
            _errorState.value = State.SUCCESS
        }
    }

    private suspend fun usePostReissueApi() {
        try {
            _reissueResult.value = Hane42Apis.hane42ApiService.postReissueRequest(accessToken)
            useGetReissueStateApi()
        } catch (err: HttpException) {
            when (err.code()) {
                404 -> {
                    _reissueState.value = null
                }
                503 -> {
                    _errorState.value = State.SERVER_FAIL
                }
            }
        } catch (e: Exception) {
            _errorState.value = State.NETWORK_FAIL
        } finally {
            _errorState.value = State.SUCCESS
        }
    }

    private suspend fun usePatchReissueFinish() {
        try {
            _reissueResult.value = Hane42Apis.hane42ApiService.patchReissueFinish(accessToken)
            useGetReissueStateApi()
        } catch (err: HttpException) {
            when (err.code()) {
                404 -> {
                    _reissueState.value = null
                }
                503 -> {
                    _errorState.value = State.SERVER_FAIL
                }
            }
        } catch (e: Exception) {
            _errorState.value = State.NETWORK_FAIL
        } finally {
            _errorState.value = State.SUCCESS
        }
    }

}