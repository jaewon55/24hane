package com.hane24.hoursarenotenough24

import android.app.Application
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
        sharedPreferenceUtils = SharedPreferenceUtils.initialize(applicationContext)
    }

    companion object {
        lateinit var instance: App
            private set

        lateinit var sharedPreferenceUtils: SharedPreferenceUtils
            private set
    }
}