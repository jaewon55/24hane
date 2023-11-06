package com.hane24.hoursarenotenough24.reissue

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentReissueDialogBinding

class ReissueWarningDialog : DialogFragment() {
    private lateinit var binding: FragmentReissueDialogBinding
    private val viewModel: ReissueViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.ReissueAlertDialogTheme)

            binding = FragmentReissueDialogBinding.inflate(layoutInflater)
                .apply {
                    lifecycleOwner = activity
                    viewModel = this@ReissueWarningDialog.viewModel
                }
            builder.setView(binding.root)

            binding.reissueDialogOkButton.setOnClickListener {
                viewModel.clickReissueOkButton()
                dialog?.cancel()
            }
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