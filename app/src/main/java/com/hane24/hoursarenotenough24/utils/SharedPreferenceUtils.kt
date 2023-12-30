package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.SharedPreferences
import com.hane24.hoursarenotenough24.App


class SharedPreferenceUtils private constructor () {
    companion object {
        private lateinit var accessTokenSharedPreferences: SharedPreferences
        private lateinit var targetTimeSharedPreferences: SharedPreferences
        fun initialize(context: Context = App.instance.applicationContext): SharedPreferenceUtils {
            if (!::accessTokenSharedPreferences.isInitialized && !::targetTimeSharedPreferences.isInitialized) {
                accessTokenSharedPreferences = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
                targetTimeSharedPreferences = context.getSharedPreferences("targetTime", Context.MODE_PRIVATE)
            }
            return SharedPreferenceUtils()
        }
    }

    fun getAccessToken() = accessTokenSharedPreferences.getString("accessToken", "")

    fun saveAccessToken(accessToken: String) {
        val editor = accessTokenSharedPreferences.edit()
        editor.putString("accessToken", "Bearer $accessToken")
        editor.apply()
    }

    fun getDayTargetTime() = targetTimeSharedPreferences.getLong("dayTargetTime", 5 * 3600).toInt()

    fun saveDayTargetTime(time: Int) {
        val editor = targetTimeSharedPreferences.edit()
        editor.putLong("dayTargetTime", time.toLong())
        editor.apply()
    }

    fun getMonthTargetTime() = targetTimeSharedPreferences.getLong("monthTargetTime", 80 * 3600).toInt()

    fun saveMonthTargetTime(time: Int) {
        val editor = targetTimeSharedPreferences.edit()
        editor.putLong("monthTargetTime", time.toLong())
        editor.apply()
    }
}