package com.hane24.hoursarenotenough24.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.network.Hane24Apis
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SplashViewModel : ViewModel() {

    private val accessToken = SharedPreferenceUtils.getAccessToken()
    private val _state = MutableLiveData<State?>()
    val state: LiveData<State?>
        get() = _state

    init {
        _state.value = null
    }

    fun checkLogin() {
        viewModelScope.launch {
            _state.value = isLogin(accessToken)
        }
    }

    suspend fun isLogin(accessToken: String?): State {
        return try {
            val result = Hane24Apis.hane24ApiService.isLogin(accessToken)

            if (result.code() == 204)
                State.SUCCESS
            else
                State.LOGIN_FAIL
        } catch (err: HttpException) {
            State.LOGIN_FAIL
        } catch (exception: Exception) {
            State.UNKNOWN_ERROR
        }
    }
}