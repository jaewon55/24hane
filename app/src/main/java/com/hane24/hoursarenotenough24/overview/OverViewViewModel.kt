package com.hane24.hoursarenotenough24.overview

import android.util.Log
import androidx.lifecycle.*
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OverViewViewModel(
    sharedPreferenceUtilss: SharedPreferenceUtilss,
    userRepository: UserRepository,
): ViewModel() {
    private val parseTimeUseCase = ParseTimeUseCase()
    private val changeTargetTimeUseCase = ChangeTargetTimeUseCase(sharedPreferenceUtilss)
    private val getUserInfoUseCase = GetUserInfoUseCase(userRepository)
    private val calculateProgressUseCase = CalculateProgressUseCase()

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private val _accumulationTime = MutableStateFlow<AccumulationTimeInfo?>(null)
    val accumulationTime: StateFlow<AccumulationTimeInfo?> = _accumulationTime.asStateFlow()

    private val _dayTargetTime = MutableStateFlow(0)
    val dayTargetTime: StateFlow<Int> =
        _dayTargetTime.map { parseTimeUseCase.parseTargetTime(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _monthTargetTime = MutableStateFlow(0)
    val monthTargetTime: StateFlow<Int> =
        _monthTargetTime.map { parseTimeUseCase.parseTargetTime(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val dayAccumulationTime: StateFlow<Pair<String, String>> =
        accumulationTime.map { parseTimeUseCase.parseAccumulationTime(it?.todayAccumulationTime) }
            .stateIn(viewModelScope, SharingStarted.Lazily, "0" to "0")

    val monthAccumulationTime: StateFlow<Pair<String, String>> =
        accumulationTime.map { parseTimeUseCase.parseAccumulationTime(it?.monthAccumulationTime) }
            .stateIn(viewModelScope, SharingStarted.Lazily, "0" to "0")

    val dayProgressPercent: StateFlow<Int> =
        accumulationTime.combine(_dayTargetTime) { acc, target ->
            calculateProgressUseCase(acc?.todayAccumulationTime, target)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val monthProgressPercent: StateFlow<Int> =
        accumulationTime.combine(_monthTargetTime) { acc, target ->
            calculateProgressUseCase(acc?.monthAccumulationTime, target)
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _mainInfo = MutableStateFlow<MainInfo?>(null)

    val intraId: StateFlow<String> =
        _mainInfo.map { it?.login ?: "marvin" }
            .stateIn(viewModelScope, SharingStarted.Lazily, "marvin")

    val profileImageUrl: StateFlow<String> =
        _mainInfo.map { it?.profileImage ?: "" }
            .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val inOutState: StateFlow<Boolean> =
        _mainInfo.map { it?.inoutState == "IN" }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val populationSeochoAndGaepo: StateFlow<Pair<Int, Int>> =
        _mainInfo.map { transformPopulationOfSeochoAndGaepo(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0 to 0)


    val initState: StateFlow<Boolean> = _mainInfo.combine(accumulationTime) { mainInfo: MainInfo?, accTime: AccumulationTimeInfo? ->
        mainInfo != null && accTime != null
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    private val _refreshLoading = MutableStateFlow(false)
    val refreshLoading: StateFlow<Boolean> = _refreshLoading.asStateFlow()


    init {
        viewModelScope.launch {
            _dayTargetTime.value = sharedPreferenceUtilss.getDayTargetTime()
            _monthTargetTime.value = sharedPreferenceUtilss.getMonthTargetTime()
            refresh()
        }
    }

    private suspend fun useGetAccumulationInfoApi() {
        try {
            _accumulationTime.value = getUserInfoUseCase.getAccumulationTime()

            _accumulationTime.value?.let {
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
            Log.i("api", "${e.message}")
            _state.value = State.NETWORK_FAIL
        } finally {
            _state.value = State.SUCCESS
        }
    }

    private suspend fun useGetMainInfoApi() {
        try {
            _mainInfo.value = getUserInfoUseCase.getInfo()
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
            Log.i("api", "${e.message}")
            _state.value = State.NETWORK_FAIL
        } finally {
            _state.value = State.SUCCESS
        }
    }

    private fun transformPopulationOfSeochoAndGaepo(mainInfo: MainInfo?): Pair<Int, Int> {
        val seocho = mainInfo?.seocho ?: 0
        val gaepo = mainInfo?.gaepo ?: 0

        return seocho to gaepo
    }

    private suspend fun refresh() {
        useGetMainInfoApi()
        useGetAccumulationInfoApi()
    }

    fun refreshButtonOnClick() = viewModelScope.launch {
        _refreshLoading.value = true
        refresh()
        _refreshLoading.value = false
    }

    private fun changeTargetTime(hour: Int, isMonth: Boolean) {
        val hoursToSeconds = hour * 3600

        if (isMonth) {
            changeTargetTimeUseCase.changeMonthTargetTime(hoursToSeconds)
            _monthTargetTime.value = hoursToSeconds
        } else {
            changeTargetTimeUseCase.changeDayTargetTime(hoursToSeconds)
            _dayTargetTime.value = hoursToSeconds
        }
    }

    fun onClickSaveTargetTime(isMonth: Boolean, selectTime: Int) {
        val currentTargetTime = if (isMonth) monthTargetTime.value else dayTargetTime.value

        if (currentTargetTime != selectTime) {
            changeTargetTime(selectTime, isMonth)
        }
    }
}

class OverViewModelFactory(
    private val sharedPreferenceUtilss: SharedPreferenceUtilss,
    private val userRepository: UserRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OverViewViewModel(sharedPreferenceUtilss, userRepository) as T
    }
}