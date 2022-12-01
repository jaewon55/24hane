package com.hane24.hoursarenotenough24.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hane24.hoursarenotenough24.App

class SharedPreferenceUtils {
    companion object {
        val sharedPreferences by lazy { init(App.instance.applicationContext) }

        fun init(context: Context): SharedPreferences {
            return context.getSharedPreferences("accessToken", Context.MODE_PRIVATE)
        }

        fun getAccessToken(): String? {
            return sharedPreferences.getString("accessToken", "")
        }

        fun saveAccessToken(accessToken: String) {
            val editor = sharedPreferences.edit()

            Log.i("data", "accessToken: $accessToken")
            editor.putString("accessToken", "Bearer $accessToken")
            editor.apply()
            Log.i("data", "preference: ${sharedPreferences?.getString("accessToken", "")}")
        }
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