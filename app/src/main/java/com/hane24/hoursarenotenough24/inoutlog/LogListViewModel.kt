package com.hane24.hoursarenotenough24.inoutlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.data.*
import com.hane24.hoursarenotenough24.network.Hane42Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogListViewModel : ViewModel() {

    private val accessToken by lazy { SharedPreferenceUtils.getAccessToken() }

    private val monthLogList = mutableListOf<MonthTimeLogContainer>()

    private var monthLogListIndex = -1

    private var selectedYear = TodayCalendarUtils.year
    private var selectedMonth = TodayCalendarUtils.month
    private val _selectedDay = MutableLiveData(1)
    val selectedDay: LiveData<Int>
        get() = _selectedDay

    private val _calendarDateText = MutableLiveData("")
    val calendarDateText: LiveData<String>
        get() = _calendarDateText

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
        "Date: $selectedYear.$selectedMonth.$day"
    }
    val dayAccumulationTimeText = Transformations.map(selectedDay) { day ->
        val time = calendarItemList.value?.getDayAccumulationTime(day) ?: 0L
        parseAccumulationTime(time)
    }
    val monthAccumulationTimeText = Transformations.map(calendarItemList) { list ->
        val time = list.getMonthAccumulationTime()
        parseAccumulationTime(time)
    }

    private val _tableItemList = MutableLiveData(emptyList<LogTableItem>())
    val tableItemList: LiveData<List<LogTableItem>>
        get() = _tableItemList




    init {
        viewModelScope.launch {
            useGetInOutInfoPerMonthApi(selectedYear, selectedMonth)
            setCalendarItemList()
            _selectedDay.value = TodayCalendarUtils.day
            setTableItemList()
        }
        setCalendarDateText()
    }


    private suspend fun useGetInOutInfoPerMonthApi(year: Int, month: Int) {
        try {
            val monthTimeLog =
                Hane42Apis.hane42ApiService.getInOutInfoPerMonth(accessToken, year, month)
                    .asDomainModel()
            monthLogList.add(MonthTimeLogContainer(monthTimeLog))
            monthLogListIndex++
        } catch (e: Exception) {
            //networkError 처리
        }
    }

    private fun parseAccumulationTime(time: Long): String {
        var second = time
        val hour = second / 3600
        second -= hour * 3600
        val min = second / 60
        return String.format("%dh %dm", hour, min)
    }

    private fun setCalendarDateText() {
        val date = Calendar.getInstance().apply { set(selectedYear, selectedMonth - 1, 1) }.time
        val monthName = SimpleDateFormat("MMM", Locale("en")).format(date)
        _calendarDateText.value = "$monthName $selectedYear"
    }

    private fun setCalendarItemList() {
        val calendar = Calendar.getInstance().apply { set(selectedYear, selectedMonth - 1, 1) }
        _calendarItemList.value = monthLogList[monthLogListIndex].getCalendarList(calendar)
    }

    private fun setTableItemList() {
        _tableItemList.value =
            monthLogList[monthLogListIndex].getLogTableList(selectedDay.value ?: 1)
    }

    fun List<CalendarItem>.getDayAccumulationTime(day: Int): Long =
        filter { it.day == day }.sumOf { it.durationTime }

    fun List<CalendarItem>.getMonthAccumulationTime(): Long =
        sumOf { it.durationTime }
}
