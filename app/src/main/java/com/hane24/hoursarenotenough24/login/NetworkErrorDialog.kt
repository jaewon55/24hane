package com.hane24.hoursarenotenough24.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class NetworkErrorDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("네트워크가 연결되지 않았습니다.\nWi-Fi 또는 데이터를 활성화 해주세요.")
                .setPositiveButton("다시시도") { _, id ->
                    val parent = activity as SplashActivity
                    parent.checkLogin()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun showNetworkErrorDialog(activity: SplashActivity) {
            val newDialog = NetworkErrorDialog()
            activity.supportFragmentManager.let {
                newDialog.show(it, "network_error_dialog")
            }
        }
    }
}