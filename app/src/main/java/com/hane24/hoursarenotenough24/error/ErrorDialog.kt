package com.hane24.hoursarenotenough24.error

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.hane24.hoursarenotenough24.R

class ErrorDialog(private val msg: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val onClick = DialogInterface.OnClickListener { _, _ -> }

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)
            builder.setMessage(msg)
                .setPositiveButton("확인", onClick)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun show(fragmentManager: FragmentManager, msg: String) {
            val newDialog = ErrorDialog(msg)
            newDialog.show(fragmentManager, "network_error_dialog")
        }
    }
}