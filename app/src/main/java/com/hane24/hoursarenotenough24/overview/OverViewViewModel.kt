package com.hane24.hoursarenotenough24.overview

import androidx.lifecycle.*
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.InfoMessage
import com.hane24.hoursarenotenough24.network.InfoMessages
import com.hane24.hoursarenotenough24.network.MainInfo
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OverViewViewModel(
    sharedPreferenceUtils: SharedPreferenceUtils,
    userRepository: UserRepository,
) : ViewModel() {

    /**
     * UseCase
     */

    private val parseTimeUseCase = ParseTimeUseCase()
    private val changeTargetTimeUseCase = ChangeTargetTimeUseCase(sharedPreferenceUtils)
    private val getUserInfoUseCase = GetUserInfoUseCase(userRepository)
    private val calculateProgressUseCase = CalculateProgressUseCase()


    /**
     * TargetTime
     */

    private val _dayTargetTime = MutableStateFlow(0)
    val dayTargetTime: StateFlow<Int> =
        _dayTargetTime.map { parseTimeUseCase.parseTargetTime(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _monthTargetTime = MutableStateFlow(0)
    val monthTargetTime: StateFlow<Int> =
        _monthTargetTime.map { parseTimeUseCase.parseTargetTime(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    /**
     * AccumulationTime
     */

    private val _accumulationTime = MutableStateFlow<AccumulationTimeInfo?>(null)
    val accumulationTime: StateFlow<AccumulationTimeInfo?> = _accumulationTime.asStateFlow()

    val dayAccumulationTime: StateFlow<Pair<String, String>> =
        accumulationTime.map { parseTimeUseCase.parseAccumulationTime(it?.todayAccumulationTime) }
            .stateIn(viewModelScope, SharingStarted.Lazily, "0" to "0")

    val monthAccumulationTime: StateFlow<Pair<String, String>> =
        accumulationTime.map { parseTimeUseCase.parseAccumulationTime(it?.monthAccumulationTime) }
            .stateIn(viewModelScope, SharingStarted.Lazily, "0" to "0")

    val acceptedAccumulationTime: StateFlow<Pair<String, String>> =
        accumulationTime.map { parseTimeUseCase.parseAccumulationTime(it?.monthlyAcceptedAccumulationTime) }
            .stateIn(viewModelScope, SharingStarted.Lazily, "0" to "0")

    val dayProgressPercent: StateFlow<Int> =
        accumulationTime.combine(_dayTargetTime) { acc, target ->
            calculateProgressUseCase(acc?.todayAccumulationTime, target)
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val accumulationGraphInfo: StateFlow<List<GraphInfo>> =
        accumulationTime.map {
            transformGraphInfo(
                it?.sixWeekAccumulationTime,
                it?.sixMonthAccumulationTime
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                listOf(
                    GraphInfo(List(6) { 0 }, false),
                    GraphInfo(List(6) { 0 }, true)
                )
            )

    /**
     *  MainInfo
     */

    private val _mainInfo = MutableStateFlow(
        MainInfo(
            login = "marvin",
            profileImage = "",
            inoutState = "OUT",
            tagAt = "2023-12-22T12:45:56.000Z",
            gaepo = 0,
            InfoMessages(
                fundInfoNotice = InfoMessage("", ""),
                tagLatencyNotice = InfoMessage("", "")
            )
        )
    )
    val mainInfo = _mainInfo.asStateFlow()

    val intraId: StateFlow<String> =
        _mainInfo.map { it.login }
            .stateIn(viewModelScope, SharingStarted.Lazily, "marvin")

    val profileImageUrl: StateFlow<String> =
        _mainInfo.map { it.profileImage }
            .stateIn(viewModelScope, SharingStarted.Lazily, "")

    val inOutState: StateFlow<Boolean> =
        _mainInfo.map { it.inoutState == "IN" }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val populationGaepo: StateFlow<Int> =
        _mainInfo.map { transformPopulationOfGaepo(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    /**
     *  State
     */

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    val initState: StateFlow<Boolean> =
        _mainInfo.combine(accumulationTime) { mainInfo: MainInfo?, accTime: AccumulationTimeInfo? ->
            mainInfo != null && accTime != null
        }.stateIn(viewModelScope, SharingStarted.Lazily, false)


    private val _refreshLoading = MutableStateFlow(false)
    val refreshLoading: StateFlow<Boolean> = _refreshLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _dayTargetTime.value = sharedPreferenceUtils.getDayTargetTime()
            _monthTargetTime.value = sharedPreferenceUtils.getMonthTargetTime()
            refresh()
        }
    }

    private suspend fun useGetAccumulationInfoApi() {
        _accumulationTime.value = getUserInfoUseCase.getAccumulationTime()
    }

    private suspend fun useGetMainInfoApi() {
        _mainInfo.value = getUserInfoUseCase.getInfo()
        _state.value = State.SUCCESS
    }

    private fun transformPopulationOfGaepo(mainInfo: MainInfo?): Int {
        return mainInfo?.gaepo ?: 0
    }

    suspend fun refresh() {
        _refreshLoading.value = true
        useGetMainInfoApi()
        useGetAccumulationInfoApi()
        _refreshLoading.value = false
    }

//    fun refreshButtonOnClick() = viewModelScope.async {
//        try {
//            _refreshLoading.value = true
//            refresh()
//            _refreshLoading.value = false
//            null
//        } catch (err: Exception) {
//            ExceptionHandlerFactory.create(err)
//        }
//    }

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

    private fun transformGraphInfo(
        timeOfWeek: List<Long>?,
        timeOfMonth: List<Long>?
    ): List<GraphInfo> {
        val default = listOf(0L, 0L, 0L, 0L, 0L, 0L)
        val weekGraphInfo = GraphInfo(timeOfWeek ?: default, false)
        val monthGraphInfo = GraphInfo(timeOfMonth ?: default, true)

        return listOf(weekGraphInfo, monthGraphInfo)
    }
}

class OverViewModelFactory(
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OverViewViewModel(sharedPreferenceUtils, userRepository) as T
    }
}