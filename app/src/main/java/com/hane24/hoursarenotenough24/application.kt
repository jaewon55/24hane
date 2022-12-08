package com.hane24.hoursarenotenough24

import android.app.Application
import android.content.Context
import com.hane24.hoursarenotenough24.error.NetworkObserver
import com.hane24.hoursarenotenough24.error.NetworkObserverImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        private val networkObserver: NetworkObserver by lazy { NetworkObserverImpl() }

        lateinit var instance: App
            private set

        private var _networkState: NetworkObserver.Status? = null
        val networkState
            get() = _networkState

        fun observeNetworkState(lifecycleScope: CoroutineScope) {
            networkObserver.observe().onEach {
                _networkState = it
            }.launchIn(lifecycleScope)
        }
    }
}