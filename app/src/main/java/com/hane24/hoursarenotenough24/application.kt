package com.hane24.hoursarenotenough24

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hane24.hoursarenotenough24.error.NetworkObserver
import com.hane24.hoursarenotenough24.error.NetworkObserverImpl
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
        sharedPreferenceUtilss = SharedPreferenceUtilss.initialize(applicationContext)
    }

    companion object {
        lateinit var instance: App
            private set

        lateinit var sharedPreferenceUtilss: SharedPreferenceUtilss
            private set
    }
}