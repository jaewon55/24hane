package com.hane24.hoursarenotenough24.overview

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewDialogBinding

class TimeDialogFragment(private val isMonth: Boolean) : DialogFragment() {
    private val binding by lazy { FragmentOverviewDialogBinding.inflate(layoutInflater) }
    private val viewModel: OverViewViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context).create()

        val numPicker = binding.dialogNumPicker

        initNumberPicker(numPicker)

        binding.dialogCancel.setOnClickListener {
            // 취소 버튼
            dialog.dismiss()
            dialog.cancel()
        }

        binding.dialogSave.setOnClickListener {
            // 확인 버튼
            viewModel.onClickSaveTargetTime(isMonth, getSelectValue(numPicker.value))
            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(binding.root)
        dialog.create()

        return dialog
    }

    private fun initNumberPicker(numPicker: NumberPicker) {
        val minValue = if (isMonth) MONTH_MIN_VALUE else DAY_MIN_VALUE
        val maxValue = if (isMonth) MONTH_MAX_VALUE else DAY_MAX_VALUE
        val step = if (isMonth) MONTH_STEP else DAY_STEP
        val currentTargetTime = if (isMonth) viewModel.monthTargetTime.value else viewModel.dayTargetTime.value

        numPicker.maxValue = getPickerIndex(minValue, maxValue, step)
        numPicker.value = getPickerIndex(minValue, currentTargetTime, step)
        numPicker.displayedValues = getDisplayedValues(numPicker.maxValue + 1, minValue, step)
    }

    private fun getPickerIndex(min: Int, max: Int, step: Int) = (max - min) / step

    private fun getDisplayedValues(count: Int, min: Int, step: Int) =
        Array(count) { "${min + it * step} 시간" }

    private fun getSelectValue(index: Int) =
        if (isMonth) (index * MONTH_STEP) + MONTH_MIN_VALUE else (index * DAY_STEP) + DAY_MIN_VALUE

    companion object {
        private const val MONTH_MAX_VALUE = 420
        private const val MONTH_MIN_VALUE = 80
        private const val MONTH_STEP = 2
        private const val DAY_MAX_VALUE = 24
        private const val DAY_MIN_VALUE = 4
        private const val DAY_STEP = 1
    }

}