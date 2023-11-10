package com.hane24.hoursarenotenough24.inoutlog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import java.util.Calendar

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
            getLogs(_year, _month)
        }
    }

    suspend fun reloadLogs(year: Int, month: Int) {
        getLogs(year, month)
    }

    fun updateDate(day: Int?) {
        day?.let { this.day = it }
    }

    fun updateDate(year: Int, month: Int, day: Int) {
        this._year = year
        this._month = month
        this.day = day
    }

    fun updateInOutState(isIn: Boolean) {
        inOutState = isIn
    }

    private suspend fun getLogs(year: Int, month: Int) {
        try {
            _loadingState.value = true
            _tagLogs = getLogsUseCase(year, month)
            _year = year
            _month = month
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