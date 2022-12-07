package com.hane24.hoursarenotenough24.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.network.Hane42Apis
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SplashViewModel : ViewModel() {

    private val accessToken = SharedPreferenceUtils.getAccessToken()
    private val _State = MutableLiveData<State?>()
    val state: LiveData<State?>
        get() = _State

    init {
        Log.i("login", "token $accessToken")
        _State.value = null
    }

    fun checkLogin() {
        viewModelScope.launch {
            _State.value = isLogin(accessToken)
        }
    }

    suspend fun isLogin(accessToken: String?): State {
        Log.i("login", "isLogin called")
        return try {
            val result = Hane42Apis.hane42ApiService.isLogin(accessToken)
            Log.i("state", "login : ${result.code()}")
            if (result.isSuccessful)
                State.SUCCESS
            else
                State.FAIL
        } catch (err: HttpException) {
            Log.i("state", "err: ${err.code()}")
            Log.i("state", "err: ${err.message()}")
            State.FAIL
        } catch (exception: Exception) {
            Log.i("state", "throw: ${exception.message}")
            State.ERROR
        }
    }
}