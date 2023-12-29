package com.hane24.hoursarenotenough24.overview

import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class ChangeTargetTimeUseCase(
    private val sharedPreferenceUtils: SharedPreferenceUtils
) {
    fun changeMonthTargetTime(time: Int) {
        sharedPreferenceUtils.saveMonthTargetTime(time)
    }

    fun changeDayTargetTime(time: Int) {
        sharedPreferenceUtils.saveDayTargetTime(time)
    }
}