package com.hane24.hoursarenotenough24.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.network.Hane42Apis
import kotlinx.coroutines.launch


class SplashViewModel: ViewModel() {

    private val accessToken = SharedPreferenceUtils.getAccessToken()
    private val _loginState = MutableLiveData<LoginState?>()
    val loginState: LiveData<LoginState?>
        get() = _loginState

    init {
        Log.i("login", "token $accessToken")
        _loginState.value = null
    }

    fun checkLogin() {
        viewModelScope.launch {
            _loginState.value = isLogin(accessToken)
        }
    }

    suspend fun isLogin(accessToken: String?): LoginState {
        Log.i("login", "isLogin called")
        return try {
            val result = Hane42Apis.hane42ApiService.isLogin(accessToken).isSuccessful
            if (result)
                LoginState.SUCCESS
            else
                LoginState.FAIL
        } catch (err: Exception) {
            LoginState.ERROR
        }
    }
}