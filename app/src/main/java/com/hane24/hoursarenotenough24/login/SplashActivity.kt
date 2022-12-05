package com.hane24.hoursarenotenough24.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.databinding.ActivitySplashBinding

enum class LoginState {
    ERROR,
    SUCCESS,
    FAIL,
}

class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setStatusAndNavigationBar()
        checkLogin()
        Log.i("login", "state ${viewModel.loginState.value}")
        viewModel.loginState.observe(this) { loginState ->
            Log.i("login", "state2 ${viewModel.loginState.value}")
            loginState?.let { checkLoginState(it) }
        }
    }

    fun checkLogin() {
        viewModel.checkLogin()
    }

    private fun checkLoginState(loginState: LoginState) {
        when (loginState) {
            LoginState.SUCCESS -> {
                Log.i("login", "success condition")

                goToMain()
            }

            LoginState.FAIL -> {
                Log.i("login", "fail condition")

                goToLogin(loginState)
            }

            LoginState.ERROR -> {
                Log.i("login", "error condition")

                NetworkErrorDialog.showNetworkErrorDialog(this)
            }
        }
    }

    private fun goToMain() {
        intent = Intent(this, MainActivity::class.java)

        startActivity(intent).also { finish() }
    }

    private fun goToLogin(loginState: LoginState) {
        val intent = Intent(this, LoginActivity::class.java)
            .putExtra("loginState", loginState)

        startActivity(intent).also { finish() }
    }

    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
}