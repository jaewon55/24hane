package com.hane24.hoursarenotenough24.error

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.hane24.hoursarenotenough24.ui.login.LoginActivity

abstract class ExceptionHandler(protected val exception: Exception) {
    abstract fun handle(context: Context)
}

class NetworkExceptionHandler(exception: Exception): ExceptionHandler(exception) {
    private val defaultMessage = "네트워크가 연결되지 않았습니다.\nWi-Fi 또는 데이터를 활성화 해주세요."
    override fun handle(context: Context) {
        val fragmentAct = context as? FragmentActivity

        fragmentAct ?: Log.e("handler", "Illegal Argument at NetworkExceptionHandler")
            .also { return }

        ErrorDialog.show(fragmentAct!!.supportFragmentManager, exception.message ?: defaultMessage)
    }
}

class ServerExceptionHandler(exception: Exception): ExceptionHandler(exception) {
    private val defaultMessage = "서버에서 알 수 없는 에러가 발생했습니다.\n증상이 계속 된다면 앱 관리자에게 문의하세요."
    override fun handle(context: Context) {
        val fragmentAct = context as? FragmentActivity

        fragmentAct ?: Log.e("handler", "Illegal Argument at ServerExceptionHandler")
            .also { return }

        ErrorDialog.show(fragmentAct!!.supportFragmentManager, exception.message ?: defaultMessage)
    }
}

class LoginExceptionHandler(exception: Exception): ExceptionHandler(exception) {
    override fun handle(context: Context) {
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        context.startActivity(intent)
    }
}

class UnknownExceptionHandler(exception: Exception): ExceptionHandler(exception) {
    private val defaultMessage = "알 수 없는 에러가 발생했습니다.\n증상이 계속 된다면 앱 관리자에게 문의하세요."
    override fun handle(context: Context) {
        val fragmentAct = context as? FragmentActivity

        fragmentAct ?: Log.e("handler", "Illegal Argument at UnknownExceptionHandler")
            .also { return }

        ErrorDialog.show(fragmentAct!!.supportFragmentManager, exception.message ?: defaultMessage)
    }
}