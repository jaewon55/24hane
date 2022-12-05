package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.hane24.hoursarenotenough24.App

object SharedPreferenceUtils {
    private val accessTokenSharedPreferences
            by lazy { init(App.instance.applicationContext, "accessToken") }
    private val dayTargetTimeSharedPreferences
            by lazy { init(App.instance.applicationContext, "dayTargetTime") }
    private val monthTargetTimeSharedPreferences
            by lazy { init(App.instance.applicationContext, "monthTargetTime") }

    private fun init(context: Context, key: String): SharedPreferences =
        context.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun getAccessToken() = accessTokenSharedPreferences.getString("accessToken", "")

    fun saveAccessToken(accessToken: String) {
        val editor = accessTokenSharedPreferences.edit()
        editor.putString("accessToken", "Bearer $accessToken")
        editor.apply()
    }

    fun getDayTargetTime() = dayTargetTimeSharedPreferences.getLong("dayTargetTime", 10 * 3600)

    fun saveDayTargetTime(time: Long) {
        val editor = dayTargetTimeSharedPreferences.edit()
        editor.putLong("dayTargetTime", time)
        editor.apply()
    }

    fun getMonthTargetTime() = dayTargetTimeSharedPreferences.getLong("monthTargetTime", 80 * 3600)

    fun saveMonthTargetTime(time: Long) {
        val editor = dayTargetTimeSharedPreferences.edit()
        editor.putLong("monthTargetTime", time)
        editor.apply()
    }
}
