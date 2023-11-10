package com.hane24.hoursarenotenough24.widget

import android.view.View
import android.widget.RemoteViews
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.AccumulationTimeInfo
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import retrofit2.HttpException

internal fun updateRefreshAnimationOn(views: RemoteViews, progressId: Int, buttonId: Int) {
    views.setViewVisibility(progressId, View.VISIBLE)
    views.setViewVisibility(buttonId, View.GONE)
}

internal fun updateRefreshAnimationOff(views: RemoteViews, progressId: Int, buttonId: Int) {
    views.setViewVisibility(progressId, View.GONE)
    views.setViewVisibility(buttonId, View.VISIBLE)
}

internal suspend fun getAccumulationInfo(): AccumulationTimeInfo? {
    return try {
        val result = Hane24Apis.hane24ApiService.getAccumulationTime(SharedPreferenceUtils.getAccessToken())
        state = State.SUCCESS
        result
    } catch (err:HttpException) {
        state = when (err.code()) {
            401 -> State.LOGIN_FAIL
            500 -> State.UNKNOWN_ERROR
            else -> State.UNKNOWN_ERROR
        }
        null
    } catch (err: Exception) {
        state = State.UNKNOWN_ERROR
        null
    }
}

internal suspend fun getInOutState(): String? {
    return try {
        val result = Hane24Apis.hane24ApiService.getMainInfo(SharedPreferenceUtils.getAccessToken()).inoutState
        state = State.SUCCESS
        result
    } catch (err:HttpException) {
        state = when (err.code()) {
            401 -> State.LOGIN_FAIL
            500 -> State.UNKNOWN_ERROR
            else -> State.UNKNOWN_ERROR
        }
        null
    } catch (err: Exception) {
        state = State.UNKNOWN_ERROR
        null
    }
}

fun getProgressPercent(accumulationTime: Long): Int {
    val targetDouble = 80.0 * 3600

    val percent = (accumulationTime / targetDouble * 100).toInt()
    if (percent >= 100) return 100
    return percent
}

fun parseTimeToday(accumulationTime: Long, isCalendarText: Boolean = false): String {
    var second = accumulationTime
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    second -= min * 60
    return if (isCalendarText) {
        String.format("%02d:%02d", hour, min)
    } else {
        String.format("%02d : %02d", hour, min)
    }
}

fun parseTimeMonth(accumulationTime: Long, isCalendarText: Boolean = false): String {
    var second = accumulationTime
    val hour = second / 3600
    second -= hour * 3600
    val min = second / 60
    second -= min * 60
    return if (isCalendarText && hour >= 100) {
        String.format("%03d:%02d", hour, min)
    } else {
        String.format("%02d : %02d", hour, min)
    }
}
