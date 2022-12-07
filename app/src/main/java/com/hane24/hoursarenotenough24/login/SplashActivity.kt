package com.hane24.hoursarenotenough24.login

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.databinding.ActivitySplashBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog

enum class State {
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
        Log.i("login", "state ${viewModel.state.value}")
        viewModel.state.observe(this) { loginState ->
            Log.i("login", "state2 ${viewModel.state.value}")
            loginState?.let { checkLoginState(it) }
        }
    }

    fun checkLogin() {
        viewModel.checkLogin()
    }

    private fun checkLoginState(state: State) {
        when (state) {
            State.SUCCESS -> {
                Log.i("login", "success condition")

                goToMain()
            }

            State.FAIL -> {
                Log.i("login", "fail condition")

                goToLogin(state)
            }

            State.ERROR -> {
                Log.i("login", "error condition")
                showErrorDialog()
            }
        }
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent).also { finish() }
    }

    private fun goToLogin(state: State) {
        val intent = Intent(this, LoginActivity::class.java)
            .putExtra("loginState", state)

        startActivity(intent).also { finish() }
    }

    private fun showErrorDialog() {
        val onClick = DialogInterface.OnClickListener { dialog, id ->
            checkLogin()
        }
        val newDialog = NetworkErrorDialog(onClick)
        supportFragmentManager.let {
            newDialog.show(it, "network_error_dialog")
        }
    }

    private fun setStatusAndNavigationBar() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
}