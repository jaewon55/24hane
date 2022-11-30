package com.hane24.hoursarenotenough24.data

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SharedPreferenceUtils(private val context: Context) {
    fun getAccessToken(): String? {
        val sharedPreferences = context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)

        return sharedPreferences.getString("accessToken", "")
    }

    fun saveAccessToken(accessToken: String) {
        val sharedPreferences = context.getSharedPreferences(
            "accessToken",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        Log.i("data", "accessToken: $accessToken")
        editor.putString("accessToken", "Bearer $accessToken")
        editor.apply()
        Log.i("data", "preference: ${sharedPreferences?.getString("accessToken", "")}")
    }
}

fun AppCompatActivity.getTargetTime(): Long {
    val sharedPreferences = getSharedPreferences("targetTime", AppCompatActivity.MODE_PRIVATE)

    return sharedPreferences.getLong("targetTime", 80 * 3600)
}

fun AppCompatActivity.saveTargetTime(time: Long) {
    val sharedPreferences =
        applicationContext.getSharedPreferences("targetTime", AppCompatActivity.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("targetTime", time)
    editor.apply()
}