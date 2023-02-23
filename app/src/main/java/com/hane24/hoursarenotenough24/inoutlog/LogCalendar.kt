package com.hane24.hoursarenotenough24.inoutlog

import androidx.lifecycle.MutableLiveData
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils

class LogCalendar {
    val year = MutableLiveData(TodayCalendarUtils.year)
    val month = MutableLiveData(TodayCalendarUtils.month)
    val day = MutableLiveData(TodayCalendarUtils.day)
}