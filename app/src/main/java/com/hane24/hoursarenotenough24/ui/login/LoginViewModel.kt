package com.hane24.hoursarenotenough24.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.network.Hane24Apis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginViewModel(sharedPreferenceUtils: SharedPreferenceUtils) : ViewModel() {

    private val accessToken = sharedPreferenceUtils.getAccessToken()
    private val _state = MutableStateFlow<Boolean>(false)
    val state: StateFlow<Boolean> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            isLogin(accessToken)
        }
    }

    private suspend fun isLogin(accessToken: String?) {
        return try {
            val result = Hane24Apis.hane24ApiService.isLogin(accessToken)

            _state.value = result.code() == 204
        } catch (err: HttpException) {
            _state.value = false
        } catch (exception: Exception) {
            _state.value = false
        }
    }
}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(private val sharedPreferenceUtils: SharedPreferenceUtils): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(sharedPreferenceUtils) as T
    }
}