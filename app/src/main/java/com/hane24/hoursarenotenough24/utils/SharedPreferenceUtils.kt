package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.hane24.hoursarenotenough24.App


class SharedPreferenceUtilss private constructor () {
    companion object {
        private lateinit var accessTokenSharedPreferences: SharedPreferences
        private lateinit var targetTimeSharedPreferences: SharedPreferences
        fun initialize(context: Context = App.instance.applicationContext): SharedPreferenceUtilss {
            if (!::accessTokenSharedPreferences.isInitialized && !::targetTimeSharedPreferences.isInitialized) {
                accessTokenSharedPreferences = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
                targetTimeSharedPreferences = context.getSharedPreferences("targetTime", Context.MODE_PRIVATE)
            }
            return SharedPreferenceUtilss()
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

object SharedPreferenceUtils {
    private val accessTokenSharedPreferences
            by lazy { init(App.instance.applicationContext, "accessToken") }
    private val targetTimeSharedPreferences
            by lazy { init(App.instance.applicationContext, "targetTime") }

    private fun init(context: Context, key: String): SharedPreferences =
        context.getSharedPreferences(key, Context.MODE_PRIVATE)

    fun getAccessToken() = accessTokenSharedPreferences.getString("accessToken", "")

    fun saveAccessToken(accessToken: String) {
        val editor = accessTokenSharedPreferences.edit()
        editor.putString("accessToken", "Bearer $accessToken")
        editor.apply()
    }

    fun getDayTargetTime() = targetTimeSharedPreferences.getLong("dayTargetTime", 5 * 3600)

    fun saveDayTargetTime(time: Long) {
        val editor = targetTimeSharedPreferences.edit()
        editor.putLong("dayTargetTime", time)
        editor.apply()
    }

    fun getMonthTargetTime() = targetTimeSharedPreferences.getLong("monthTargetTime", 80 * 3600)

    fun saveMonthTargetTime(time: Long) {
        val editor = targetTimeSharedPreferences.edit()
        editor.putLong("monthTargetTime", time)
        editor.apply()
    }
}
