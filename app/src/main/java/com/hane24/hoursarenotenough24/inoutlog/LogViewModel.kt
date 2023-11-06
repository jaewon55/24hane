package com.hane24.hoursarenotenough24.inoutlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LogViewModel(
    private val getLogsUseCase: GetLogsUseCase =
        GetLogsUseCase(
            TimeServerRepository(
                Hane24Apis.hane24ApiService,
                SharedPreferenceUtils
            ),
            TimeDBRepository(createDatabase())
        )
) : ViewModel() {

    private val _year = MutableStateFlow(TodayCalendarUtils.year)
    val year: StateFlow<Int>
        get() = _year

    private val _month = MutableStateFlow(TodayCalendarUtils.month)
    val month: StateFlow<Int>
        get() = _month

    private val _day = MutableStateFlow(TodayCalendarUtils.day)
    val day: StateFlow<Int>
        get() = _day

    private val _tagLogs = MutableStateFlow<List<TagLog>>(emptyList())
    val tagLogs: StateFlow<List<TagLog>>
        get() = _tagLogs

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private val _errorState = MutableStateFlow(State.SUCCESS)
    val errorSate: StateFlow<State>
        get() = _errorState

    private val _inOutState = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            getLogs(
                _year.value ?: TodayCalendarUtils.year,
                _month.value ?: TodayCalendarUtils.month,
                _day.value ?: TodayCalendarUtils.day
            )
        }
    }

    suspend fun reloadLogs(year: Int, month: Int, day: Int = 1) {
        getLogs(year, month, day)
    }

    fun calendarItemOnClick(day: Int) {
        _day.value = day
    }

    fun setInOutState(isIn: Boolean) {
        _inOutState.value = isIn
    }

    private suspend fun getLogs(year: Int, month: Int, day: Int) {
        try {
            _loadingState.value = true
            _tagLogs.value = getLogsUseCase(year, month)
            _year.value = year
            _month.value = month
            _day.value = day
        } catch (err: HttpException) {
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500

            when {
                isLoginFail -> _errorState.value = State.LOGIN_FAIL
                isServerError -> _errorState.value = State.SERVER_FAIL
                else -> _errorState.value = State.UNKNOWN_ERROR
            }
        } catch (err: Exception) {
            _errorState.value = State.NETWORK_FAIL
        } finally {
            _errorState.value = State.SUCCESS
            _loadingState.value = false
        }
    }
}