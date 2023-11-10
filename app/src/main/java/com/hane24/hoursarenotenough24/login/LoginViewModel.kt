package com.hane24.hoursarenotenough24.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.network.Hane24Apis
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtilss
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class LoginViewModel(sharedPreferenceUtilss: SharedPreferenceUtilss) : ViewModel() {

    private val accessToken = sharedPreferenceUtilss.getAccessToken()
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
class LoginViewModelFactory(private val sharedPreferenceUtilss: SharedPreferenceUtilss): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(sharedPreferenceUtilss) as T
    }
}