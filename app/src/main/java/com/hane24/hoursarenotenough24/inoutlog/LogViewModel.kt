package com.hane24.hoursarenotenough24.inoutlog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.data.TagLog
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.repository.TimeDBRepository
import com.hane24.hoursarenotenough24.repository.TimeServerRepository
import com.hane24.hoursarenotenough24.repository.UserRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.Calendar

class LogViewModel(
    timeServerRepository: TimeServerRepository,
    timeDBRepository: TimeDBRepository
) : ViewModel() {
    private val getLogsUseCase =
        GetLogsUseCase(timeServerRepository, timeDBRepository)
    private val deleteAllLogsUseCase =
        DeleteAllLogsUseCase(timeDBRepository)

    private var _year = TodayCalendarUtils.year
    val year: Int
        get() = _year

    private var _month = TodayCalendarUtils.month
    val month: Int
        get() = _month

    var day by mutableIntStateOf(TodayCalendarUtils.day)
        private set

    private var _tagLogs: List<TagLog> = emptyList()
    val tagLogs: List<TagLog>
        get() = _tagLogs

    val tagLogsOfTheDay: List<TagLog>
        get() {
            val calendar = Calendar.getInstance()
            return _tagLogs.filter {
                val timeStamp = it.inTimeStamp ?: it.outTimeStamp ?: 0L
                if (timeStamp != 0L) {
                    calendar.timeInMillis = timeStamp * 1000
                    calendar.get(Calendar.DAY_OF_MONTH) == day
                } else {
                    false
                }
            }
        }

    private var _totalAccumulationTime = 0L
    val totalAccumulationTime: Long
        get() = _totalAccumulationTime

    private var _acceptedAccumulationTime = 0L
    val acceptedAccumulationTime: Long
        get() = _acceptedAccumulationTime

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private val _errorState = MutableStateFlow(State.SUCCESS)
    val errorSate: StateFlow<State>
        get() = _errorState

    var inOutState by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            getLogs(_year, _month, day)
        }
    }

    suspend fun reloadLogs(year: Int, month: Int) {
        getLogs(year, month, day)
    }

    fun updateDay(day: Int?) {
        day?.let { this.day = it }
    }

    fun updateLogs(year: Int, month: Int, day: Int = 1) {
        if (year == 2022 && month < 8) return
        if (year > TodayCalendarUtils.year ||
            (year == TodayCalendarUtils.year && month > TodayCalendarUtils.month)
        ) {
            return
        }
        viewModelScope.launch {
            getLogs(year, month, day)
        }
    }

    fun updateInOutState(isIn: Boolean) {
        inOutState = isIn
    }

    fun deleteAllLogsInDatabase() {
        viewModelScope.launch { deleteAllLogsUseCase() }
    }

    private suspend fun getLogs(year: Int, month: Int, day: Int) {
        val yearBeforeChange = _year
        val monthBeforeChange = _month

        try {
            _year = year
            _month = month
            _loadingState.value = true
            getLogsUseCase(year, month).also {
                _tagLogs = it.tagLogs
                _totalAccumulationTime = it.totalAccumulationTime
                _acceptedAccumulationTime = it.acceptedAccumulationTime
            }
            this.day = day
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
            if (_errorState.value != State.SUCCESS) {
                _year = yearBeforeChange
                _month = monthBeforeChange
                _errorState.value = State.SUCCESS
            }
            _loadingState.value = false
        }
    }
}

class LogViewModelFactory(
    private val timeServerRepository: TimeServerRepository,
    private val timeDBRepository: TimeDBRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LogViewModel(timeServerRepository, timeDBRepository) as T
    }
}

