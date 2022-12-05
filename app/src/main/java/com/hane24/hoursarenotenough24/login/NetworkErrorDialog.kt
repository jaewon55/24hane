package com.hane24.hoursarenotenough24.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hane24.hoursarenotenough24.R

class NetworkErrorDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val onclick = DialogInterface.OnClickListener { dialog, id ->
            val parent = activity as SplashActivity
            parent.checkLogin()
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.setMessage("네트워크가 연결되지 않았습니다.\nWi-Fi 또는 데이터를 활성화 해주세요.")
                .setPositiveButton("다시 시도", onclick)
                .create()
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