package com.hane24.hoursarenotenough24.inoutlog

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.data.*
import com.hane24.hoursarenotenough24.database.asDomainModel
import com.hane24.hoursarenotenough24.database.createDatabase
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.network.asDomainModel
import com.hane24.hoursarenotenough24.repository.TimeRepository
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class LogListViewModel : ViewModel() {

    private val monthLogContainer = mutableListOf<MonthTimeLogContainer>()

    private val logCalendar = LogCalendar()

    private var monthLogListIndex = -1

    private val repository = TimeRepository(createDatabase())

    private var selectedYear = TodayCalendarUtils.year
    private var selectedMonth = TodayCalendarUtils.month
        set(value) {
            field = when {
                value > 12 -> {
                    selectedYear++
                    1
                }
                value < 1 -> {
                    selectedYear--
                    12
                }
                else -> value
            }
        }

    private val _selectedDay = MutableLiveData(TodayCalendarUtils.day)
    val selectedDay: LiveData<Int>
        get() = _selectedDay

    private val _calendarDateText = MutableLiveData("")
    val calendarDateText = Transformations.map(logCalendar.month) { m ->
        String.format("%d%d", logCalendar.year.value, m)
    }

    private val _leftButtonState = MutableLiveData(true)
    val leftButtonState: LiveData<Boolean>
        get() = _leftButtonState

    private val _rightButtonState = MutableLiveData(false)
    val rightButtonState: LiveData<Boolean>
        get() = _rightButtonState

    private val _calendarItemList = MutableLiveData(emptyList<CalendarItem>())
    val calendarItemList: LiveData<List<CalendarItem>>
        get() = _calendarItemList

    val selectedDateText = Transformations.map(selectedDay) { day ->
        val date = Calendar.getInstance().apply { set(selectedYear, selectedMonth - 1, day) }.time
        SimpleDateFormat("M.d EEEE", Locale("ko")).format(date)
    }
    val dayAccumulationTimeText = Transformations.map(selectedDay) { day ->
        val time = calendarItemList.value?.getDayAccumulationTime(day) ?: 0L
        parseAccumulationTime(time)
    }
    val monthAccumulationTimeText = Transformations.map(calendarItemList) { list ->
        val time = list.getMonthAccumulationTime()
        "총 ${parseAccumulationTime(time)}"
    }

    private val _tableItemList = MutableLiveData(emptyList<LogTableItem>())
    val tableItemList: LiveData<List<LogTableItem>>
        get() = _tableItemList

    private val _loadingState = MutableLiveData(true)
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _refreshLoading = MutableLiveData(false)
    val refreshLoading: LiveData<Boolean>
        get() = _refreshLoading

    private val _errorState = MutableLiveData<State?>(null)
    val errorState: LiveData<State?>
        get() = _errorState

    init {
        viewModelScope.launch {
            _loadingState.value = false
            useGetInOutInfoPerMonthApi(selectedYear, selectedMonth)
            _selectedDay.value = TodayCalendarUtils.day
            _loadingState.value = false
        }
        setCalendarDateText()
    }

    private suspend fun useGetInOutInfoPerMonthApi(year: Int, month: Int) {
        try {
            val monthTimeLog = withContext(Dispatchers.IO) {
                repository.getMonth(String.format("%4d%02d", year, month)).asDomainModel()
            }
//                Hane42Apis.hane42ApiService.getInOutInfoPerMonth(accessToken, year, month)
//                    .asDomainModel()
            monthLogContainer.add(MonthTimeLogContainer(year, month, monthTimeLog))
            monthLogListIndex++
            setCalendarItemList()
            setTableItemList()
        } catch (err: HttpException) {
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500

            when {
                isLoginFail -> _errorState.value = State.LOGIN_FAIL
                isServerError -> _errorState.value = State.SERVER_FAIL
                else -> _errorState.value = State.UNKNOWN_ERROR
            }
        } catch (err: Exception) {
            Log.e("e_msg", err.message ?: "몰라")
            _errorState.value = State.UNKNOWN_ERROR
        }
    }

    private suspend fun refreshMonthLog() {
        try {
            val newYear = TodayCalendarUtils.year
            val newMonth = TodayCalendarUtils.month
            val newLogs = withContext(Dispatchers.IO) {
                repository.getMonth(String.format("%4d%02d", newYear, newMonth)).asDomainModel()
            }
//                Hane42Apis.hane42ApiService.getInOutInfoPerMonth(accessToken, newYear, newMonth)
//                    .asDomainModel()
            val container = monthLogContainer.find { it.year == newYear && it.month == newMonth }
            if (container == null) {
                monthLogContainer.add(0, MonthTimeLogContainer(newYear, newMonth, newLogs))
                monthLogListIndex++
            } else {
                container.monthLog = newLogs
                if (selectedYear == newYear && selectedMonth == newMonth) {
                    setCalendarItemList()
                    setTableItemList()
                }
            }
        } catch (err: HttpException) {
            val isLoginFail = err.code() == 401
            val isServerError = err.code() == 500

            when {
                isLoginFail -> _errorState.value = State.LOGIN_FAIL
                isServerError -> _errorState.value = State.SERVER_FAIL
                else -> _errorState.value = State.UNKNOWN_ERROR
            }
        } catch (err: Exception) {
            _errorState.value = State.UNKNOWN_ERROR
        }
    }

    private fun parseAccumulationTime(time: Long): String {
        var second = time
        val hour = second / 3600
        second -= hour * 3600
        val min = second / 60
        return String.format("%d시간 %d분", hour, min)
    }

    private fun setCalendarDateText() {
//        val date = Calendar.getInstance().apply { set(selectedYear, selectedMonth - 1, 1) }.time
//        val monthName = SimpleDateFormat("MMM", Locale("en")).format(date)
        _calendarDateText.value = "$selectedYear.$selectedMonth"
    }

    private fun setCalendarItemList() {
        val index = if (monthLogListIndex < 0) 0 else monthLogListIndex
        _calendarItemList.value =
            monthLogContainer[index].getCalendarList()
    }

    private fun setTableItemList() {
        val index = if (monthLogListIndex < 0) 0 else monthLogListIndex
        _tableItemList.value =
            monthLogContainer[index].getLogTableList(selectedDay.value ?: 1)
    }

    private fun setButtonState() {
        _leftButtonState.value = selectedYear != 2022 || selectedMonth > 8
        _rightButtonState.value =
            selectedYear != TodayCalendarUtils.year || selectedMonth != TodayCalendarUtils.month
    }

    private fun getNewMonthData() {
        viewModelScope.launch {
            try {
                val monthTimeLog = withContext(Dispatchers.IO) {
                    repository.getMonth(String.format("%4d%02d", selectedYear, selectedMonth)).asDomainModel()
                }
//                    Hane42Apis.hane42ApiService.getInOutInfoPerMonth(
//                        accessToken,
//                        selectedYear,
//                        selectedMonth
//                    )
//                        .asDomainModel()
                monthLogContainer.add(
                    MonthTimeLogContainer(
                        selectedYear,
                        selectedMonth,
                        monthTimeLog
                    )
                )
                monthLogListIndex++
            } catch (e: Exception) {
                selectedMonth++
                _errorState.value = State.UNKNOWN_ERROR
            } finally {
                setCalendarDateText()
                setCalendarItemList()
                setTableItemList()
                _selectedDay.value = 1
                _loadingState.value = false
                setButtonState()
            }
        }
    }

    fun changeSelectedDay(day: Int) {
        _selectedDay.value = day
        setTableItemList()
    }

    fun leftButtonOnClick() {
        _leftButtonState.value = false
        _rightButtonState.value = false
        _loadingState.value = false
        selectedMonth--
        setCalendarDateText()
        if (monthLogContainer.lastIndex == monthLogListIndex) {
            getNewMonthData()
        } else {
            monthLogListIndex++
            setCalendarItemList()
            setTableItemList()
            _selectedDay.value = 1
            _loadingState.value = false
            setButtonState()
        }
    }

    fun rightButtonOnClick() {
        _leftButtonState.value = false
        _rightButtonState.value = false
        _loadingState.value = false
        selectedMonth++
        setCalendarDateText()
        _selectedDay.value = 1
        monthLogListIndex--
        setCalendarItemList()
        setTableItemList()
        _selectedDay.value = 1
        _loadingState.value = false
        setButtonState()
    }

    fun refreshButtonOnClick() {
        _refreshLoading.value = false
        viewModelScope.launch {
            refreshMonthLog()
            _refreshLoading.value = false
        }
    }

    private fun List<CalendarItem>.getDayAccumulationTime(day: Int): Long =
        filter { it.day == day }.sumOf { it.durationTime }

    private fun List<CalendarItem>.getMonthAccumulationTime(): Long =
        sumOf { it.durationTime }
}
