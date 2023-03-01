package com.hane24.hoursarenotenough24.reissue

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentReissueDialogBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog

class ReissueWarningDialog: DialogFragment() {
    private val binding by lazy { FragmentReissueDialogBinding.inflate(layoutInflater) }
    private val viewModel: ReissueViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogTheme)

            builder.setView(binding.root)

            binding.reissueDialogOkButton.setOnClickListener { viewModel.clickReissueOkButton() }
            binding.reissueDialogCancelButton.setOnClickListener { dialog?.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun showReissueDialog(fragmentManager: FragmentManager) {
            val newDialog = ReissueWarningDialog()
            newDialog.show(fragmentManager, "reissue_dialog")
        }
    }
}