package com.hane24.hoursarenotenough24.overview

import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss

class ChangeTargetTimeUseCase(
    private val sharedPreferenceUtils: SharedPreferenceUtilss
) {
    fun changeMonthTargetTime(time: Int) {
        sharedPreferenceUtils.saveMonthTargetTime(time)
    }

    fun changeDayTargetTime(time: Int) {
        sharedPreferenceUtils.saveDayTargetTime(time)
    }
}