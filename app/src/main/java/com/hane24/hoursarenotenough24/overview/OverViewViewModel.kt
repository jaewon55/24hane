package com.hane24.hoursarenotenough24.overview

import android.util.Log
import androidx.lifecycle.*
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch

class OverViewViewModel : ViewModel() {

    private val _state = MutableLiveData<State?>(null)
    val state: LiveData<State?>
        get() = _state

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

    private val _intraId = MutableLiveData("")
    val intraId: LiveData<String>
        get() = _intraId

    private val _dayAccumulationTime = MutableLiveData(0L)
    val dayAccumulationTime: LiveData<String> =
        Transformations.map(_dayAccumulationTime) { parseTime(it) }

    private val _dayTargetTime = MutableLiveData(0L)
    val dayTargetTime: LiveData<String> =
        Transformations.map(_dayTargetTime) { parseTime(it) }

    private val _dayProgressPercent = MutableLiveData(0)
    val dayProgressPercent: LiveData<Int>
        get() = _dayProgressPercent
    val dayProgressPercentText: LiveData<String> =
        Transformations.map(_dayProgressPercent) { "$it%" }

    private val _monthAccumulationTime = MutableLiveData(0L)
    val monthAccumulationTime: LiveData<String> =
        Transformations.map(_monthAccumulationTime) { parseTime(it) }

    private val _monthTargetTime = MutableLiveData(0L)
    val monthTargetTime: LiveData<String> =
        Transformations.map(_monthTargetTime) { parseTime(it) }

    private val _monthProgressPercent = MutableLiveData(0)
    val monthProgressPercent: LiveData<Int>
        get() = _monthProgressPercent
    val monthProgressPercentText: LiveData<String> =
        Transformations.map(_monthProgressPercent) { "$it%" }

    private val _inOutState = MutableLiveData(false)
    val inOutState: LiveData<Boolean>
        get() = _inOutState

    init {
        _dayTargetTime.value = SharedPreferenceUtils.getDayTargetTime()
        _monthTargetTime.value = SharedPreferenceUtils.getMonthTargetTime()
        viewModelScope.launch {
            useGetMainInfoApi()
            useGetAccumulationInfoApi()
        }
    }

    private suspend fun useGetAccumulationInfoApi() {
        try {
            val accumulationTime = Hane42Apis.hane42ApiService.getAccumulationTime(accessToken)
            _monthAccumulationTime.value = accumulationTime.monthAccumulationTime
            _dayAccumulationTime.value = accumulationTime.todayAccumulationTime
            _dayProgressPercent.value =
                getProgressPercent(accumulationTime.todayAccumulationTime, _dayTargetTime.value)
            _monthProgressPercent.value =
                getProgressPercent(accumulationTime.monthAccumulationTime, _monthTargetTime.value)
            _state.value = State.SUCCESS
        } catch (e: Exception) {
            //networkError 처리
            Log.i("state", "message: ${e.message}")
            _state.value = State.ERROR
        }
    }

    private suspend fun useGetMainInfoApi() {
        return try {
            val mainInfo = Hane42Apis.hane42ApiService.getMainInfo(accessToken)
            _intraId.value = mainInfo.login
            _inOutState.value = mainInfo.inoutState == "IN"
            _state.value = State.SUCCESS
        } catch (e: Exception) {
            //networkError 처리
            Log.i("state", "message: ${e.message}")
            _state.value = State.ERROR
        }
    }

    private fun getProgressPercent(time: Long, targetTime: Long?): Int {
        val targetDouble: Double = targetTime?.toDouble() ?: 0.0
        val percent = (time / targetDouble * 100).toInt()
        if (percent >= 100) return 100
        return percent
    }
}

fun parseTime(time: Long): String {
    var second = time
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    return String.format("%d:%02d", hour, min)
}
