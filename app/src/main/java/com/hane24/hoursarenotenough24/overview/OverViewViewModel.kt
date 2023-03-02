package com.hane24.hoursarenotenough24.overview

import android.util.Log
import androidx.lifecycle.*
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.ClusterPopulationInfo
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OverViewViewModel : ViewModel() {

    private val _state = MutableLiveData<State?>(null)
    val state: LiveData<State?>
        get() = _state

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

    private val _intraId = MutableLiveData("")
    val intraId: LiveData<String>
        get() = _intraId

    private val _profileImageUrl = MutableLiveData("")
    val profileImageUrl: LiveData<String>
        get() = _profileImageUrl

    private val _dayAccumulationTime = MutableLiveData(0L)
    val dayAccumulationTime: LiveData<Pair<String, String>> =
        Transformations.map(_dayAccumulationTime) { parseTime(it, false) }

    private val _dayTargetTime = MutableLiveData(0L)
    val dayTargetTime: LiveData<Pair<String, String>> =
        Transformations.map(_dayTargetTime) { parseTime(it, true) }

    private val _dayProgressPercent = MutableLiveData(0)
    val dayProgressPercent: LiveData<Int>
        get() = _dayProgressPercent
    val dayProgressPercentText: LiveData<String> =
        Transformations.map(_dayProgressPercent) { it.toString() }

    private val _monthAccumulationTime = MutableLiveData(0L)
    val monthAccumulationTime: LiveData<Pair<String, String>> =
        Transformations.map(_monthAccumulationTime) { parseTime(it, false) }

    private val _monthTargetTime = MutableLiveData(0L)
    val monthTargetTime: LiveData<Pair<String, String>> =
        Transformations.map(_monthTargetTime) { parseTime(it, true) }

    private val _monthProgressPercent = MutableLiveData(0)
    val monthProgressPercent: LiveData<Int>
        get() = _monthProgressPercent

    val monthProgressPercentText: LiveData<String> =
        Transformations.map(_monthProgressPercent) { it.toString() }

    private val _latestTagTime = MutableLiveData("")
    val latestTagTime: LiveData<String>
        get() = _latestTagTime

    private val _inOutState = MutableLiveData(false)
    val inOutState: LiveData<Boolean>
        get() = _inOutState

    private val _initState = MutableLiveData(false)
    val initState: LiveData<Boolean>
        get() = _initState

    private val _refreshLoading = MutableLiveData(false)
    val refreshLoading: LiveData<Boolean>
        get() = _refreshLoading

    private val _clusterPopulation: MutableLiveData<List<ClusterPopulationInfo>?> = MutableLiveData(null)
    val clusterPopulation: LiveData<List<ClusterPopulationInfo>?>
        get() = _clusterPopulation

    private val _accumulationTime: MutableLiveData<AccumulationTimeInfo?> = MutableLiveData(null)
    val accumulationTime: LiveData<AccumulationTimeInfo?>
        get() = _accumulationTime

    init {
        _dayTargetTime.value = SharedPreferenceUtils.getDayTargetTime()
        _monthTargetTime.value = SharedPreferenceUtils.getMonthTargetTime()
        viewModelScope.launch {
            useGetMainInfoApi()
            useGetAccumulationInfoApi()
            useGetCadetPerClusterApi()
            _initState.value = true
        }
    }

    private suspend fun useGetAccumulationInfoApi() {
        try {
            _accumulationTime.value = Hane42Apis.hane42ApiService.getAccumulationTime(accessToken)
            Log.i("accumulation", "${accumulationTime.value}")

            _accumulationTime.value?.let {
                _monthAccumulationTime.value = it.monthAccumulationTime
                _dayAccumulationTime.value = it.todayAccumulationTime
                _dayProgressPercent.value =
                    getProgressPercent(it.todayAccumulationTime, _dayTargetTime.value)
                _monthProgressPercent.value =
                    getProgressPercent(it.monthAccumulationTime, _monthTargetTime.value)
                _state.value = State.SUCCESS
            }


        } catch (err: HttpException) {
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500

            when {
                isLoginFail -> _state.value = State.LOGIN_FAIL
                isServerError -> _state.value = State.SERVER_FAIL
                else -> _state.value = State.UNKNOWN_ERROR
            }
        } catch (e: Exception) {
            _state.value = State.UNKNOWN_ERROR
        }
    }

    private suspend fun useGetCadetPerClusterApi() {
        try {
            _clusterPopulation.value = Hane42Apis.hane42ApiService.getCadetPerCluster(accessToken)
            _state.value = State.SUCCESS
            Log.i("api", "${_clusterPopulation.value}")
        } catch (err: HttpException) {
            Log.i("api", "${err.message()}")
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500
            when {
                isLoginFail -> _state.value = State.LOGIN_FAIL
                isServerError -> _state.value = State.SERVER_FAIL
                else -> _state.value = State.UNKNOWN_ERROR
            }
        } catch (e: Exception) {
            Log.i("api", "${e.message}")
            _state.value = State.UNKNOWN_ERROR
        }
    }

    private suspend fun useGetMainInfoApi() {
        try {
            val mainInfo = Hane42Apis.hane42ApiService.getMainInfo(accessToken)
            _intraId.value = mainInfo.login
            _profileImageUrl.value = mainInfo.profileImage
            if (mainInfo.inoutState == "IN") {
                _inOutState.value = true
                _latestTagTime.value = mainInfo.tagAt
                    .substringAfter('T')
                    .split(":")
                    .let {
                        var hour = it[0].toInt() + 9
                        if (hour > 23) hour -= 24
                        "$hour:${it[1]}"
                    }
            } else {
                _inOutState.value = false
                _latestTagTime.value = ""
            }
            _state.value = State.SUCCESS
        } catch (err: HttpException) {
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500

            when {
                isLoginFail -> _state.value = State.LOGIN_FAIL
                isServerError -> _state.value = State.SERVER_FAIL
                else -> _state.value = State.UNKNOWN_ERROR
            }
        } catch (e: Exception) {
            _state.value = State.UNKNOWN_ERROR
        }
    }

    fun getSeochoPopulation(): LiveData<String> {
        return Transformations.map(_clusterPopulation) {
            if (clusterPopulation.value == null) "000명" else "${clusterPopulation.value!![0].population}명"
        }
    }

    fun getGaepoPopulation(): LiveData<String> {
        return Transformations.map(_clusterPopulation) {
            if (clusterPopulation.value == null) "000명" else "${clusterPopulation.value!![1].population}명"
        }
    }

    fun refreshButtonOnClick() {
        _refreshLoading.value = true
        viewModelScope.launch {
            useGetMainInfoApi()
            useGetAccumulationInfoApi()
            _refreshLoading.value = false
        }
    }

    fun changeTargetTime(time: Long, isMonth: Boolean) {
        if (isMonth) {
            SharedPreferenceUtils.saveMonthTargetTime(time)
            _monthTargetTime.value = time
            _monthProgressPercent.value =
                getProgressPercent(_monthAccumulationTime.value ?: 0, time)
        } else {
            SharedPreferenceUtils.saveDayTargetTime(time)
            _dayTargetTime.value = time
            _dayProgressPercent.value =
                getProgressPercent(_dayAccumulationTime.value ?: 0, time)
        }
    }

    fun getMTargetTime() = _monthTargetTime.value

    fun getDTargetTime() = _dayTargetTime.value

    fun parseTimeToPercent(items: List<Long>): List<Long> {
        val maxItem = items.maxOf { it }
        return items.map{ (it.toDouble() / maxItem * 100L).toLong() }
    }

    private fun getProgressPercent(time: Long, targetTime: Long?): Int {
        val targetDouble: Double = targetTime?.toDouble() ?: 1.0
        return (time / targetDouble * 100).toInt()
    }

    private fun parseTime(time: Long, isTargetTime: Boolean): Pair<String, String> {
        var second = time
        val hour = second / 3600
        if (isTargetTime)
            return String.format("%d", hour) to "H"
        second -= hour * 3600
        val min = second / 60
        return String.format("%d", hour) to String.format("%d", min)
    }
}
