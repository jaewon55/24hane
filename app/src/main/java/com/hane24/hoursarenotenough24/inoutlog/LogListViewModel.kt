package com.hane24.hoursarenotenough24.inoutlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.data.*
import com.hane24.hoursarenotenough24.database.asDomainModel
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.repository.TimeRepositoryC
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class LogListViewModel : ViewModel() {

    private val repository = TimeRepositoryC(createDatabase())

    private val _calendarYear = MutableLiveData(TodayCalendarUtils.year)
    val calendarYear: LiveData<Int>
        get() = _calendarYear

    private val _calendarMonth = object : MutableLiveData<Int>(TodayCalendarUtils.month) {
        override fun setValue(value: Int?) {
            val m = when {
                value == null -> null
                value > 12 -> {
                    _calendarYear.value = _calendarYear.value?.plus(1)
                    1
                }
                value < 1 -> {
                    _calendarYear.value = _calendarYear.value?.minus(1)
                    12
                }
                else -> value
            }
            super.setValue(m)
        }
    }
    val calendarMonth: LiveData<Int>
        get() = _calendarMonth

    private val _calendarDay = MutableLiveData(TodayCalendarUtils.day)
    val calendarDay: LiveData<Int>
        get() = _calendarDay

    private val logContainer = MutableLiveData<MonthTimeLogContainer>()

    val calendarList = Transformations.map(logContainer) { list ->
        list.getCalendarList()
    }

    val calendarDateText = Transformations.map(_calendarMonth) {
        String.format("%d.%d", _calendarYear.value, _calendarMonth.value)
    }

    val monthAccumulationTime = Transformations.map(calendarList) {
        val time = calendarList.value?.sumOf { it.durationTime } ?: 0
        "총 ${parseAccumulationTime(time)}"
    }

    val logTableList = Transformations.map(calendarDay) {
        logContainer.value?.getLogTableList(it, _inOutState.value ?: true) ?: emptyList()
    }

    val selectedDateText = Transformations.map(calendarDay) {
        val date = Calendar.getInstance()
            .apply {
                set(
                    _calendarYear.value ?: TodayCalendarUtils.year,
                    (_calendarMonth.value ?: TodayCalendarUtils.month) - 1,
                    it
                )
            }.time
        SimpleDateFormat("M.d EEEE", Locale("ko")).format(date)
    }

    val dayAccumulationTime = Transformations.map(logTableList) {
        val time = logContainer.value?.monthLog
            ?.filter { it.day == _calendarDay.value }
            ?.sumOf { it.durationTime ?: 0 } ?: 0
        parseAccumulationTime(time)
    }

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _errorState = MutableLiveData<State?>(null)
    val errorState: LiveData<State?>
        get() = _errorState

    private val _inOutState = MutableLiveData<Boolean>(true)

    init {
        viewModelScope.launch {
            inOutInfoPerMonthApi(TodayCalendarUtils.year, TodayCalendarUtils.month)
        }
    }

    fun leftButtonOnClick() {
        viewModelScope.launch {
            var newYear = _calendarYear.value ?: TodayCalendarUtils.year
            val newMonth = _calendarMonth.value?.minus(1)?.let {
                if (it == 0) {
                    --newYear
                    12
                } else {
                    it
                }
            } ?: TodayCalendarUtils.month
            inOutInfoPerMonthApi(newYear, newMonth)
            if (_calendarYear.value != newYear) {
                _calendarYear.value = newYear
            }
            _calendarMonth.value = newMonth
            _calendarDay.value = 1
        }
    }

    fun rightButtonOnClick() {
        viewModelScope.launch {
            var newYear = _calendarYear.value ?: TodayCalendarUtils.year
            val newMonth = _calendarMonth.value?.plus(1)?.let {
                if (it == 13) {
                    ++newYear
                    1
                } else {
                    it
                }
            } ?: TodayCalendarUtils.month
            val autoUpdate =
                newYear != TodayCalendarUtils.year || newMonth != TodayCalendarUtils.month
            inOutInfoPerMonthApi(newYear, newMonth, autoUpdate)
            if (_calendarYear.value != newYear) {
                _calendarYear.value = newYear
            }
            _calendarMonth.value = newMonth
            _calendarDay.value = 1
        }
    }

    fun changeCalendarDate(year: Int, month: Int, day: Int, autoUpdate: Boolean = false) {
        viewModelScope.launch {
            inOutInfoPerMonthApi(year, month, autoUpdate)
            if (_calendarYear.value != year) _calendarYear.value = year
            _calendarMonth.value = month
            _calendarDay.value = day
        }
    }

    fun refreshButtonOnClick() {
        viewModelScope.launch {
            inOutInfoPerMonthApi(
                _calendarYear.value ?: TodayCalendarUtils.year,
                (_calendarMonth.value ?: TodayCalendarUtils.month)
            )
            _calendarDay.value = _calendarDay.value
        }
    }

    fun calendarItemOnClick(day: Int) {
        _calendarDay.value = day
    }

    fun setInOutState(isIn: Boolean) {
        _inOutState.value = isIn
    }

    private suspend fun inOutInfoPerMonthApi(y: Int, m: Int, autoUpdate: Boolean = true) {
        try {
            val format = String.format("%4d%02d", y, m)
            val domainModel = withContext(Dispatchers.IO) {
                if (autoUpdate) {
                    repository.getMonthOrNull(format)?.asDomainModel()
                } else {
                    repository.getMonthNoneUpdate(format).asDomainModel()
                }
            }
            domainModel?.let {
                logContainer.value = MonthTimeLogContainer(y, m, it)
                return
            }
            _loadingState.value = true
            logContainer.value =
                MonthTimeLogContainer(y, m, repository.getMonthFromServer(format).asDomainModel())
            _loadingState.value = false
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

    private fun parseAccumulationTime(time: Long): String {
        var second = time
        val hour = second / 3600
        second -= hour * 3600
        val min = second / 60
        return String.format("%d시간 %d분", hour, min)
    }

}
