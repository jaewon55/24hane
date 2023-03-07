package com.hane24.hoursarenotenough24.inoutlog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hane24.hoursarenotenough24.databinding.FragmentLogListDialogBinding
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import kotlin.math.max
import kotlin.math.min

class CalendarDialog : DialogFragment() {
    private val binding by lazy { FragmentLogListDialogBinding.inflate(layoutInflater) }
    private val viewModel: LogListViewModel by activityViewModels()
    private val yearPicker by lazy { binding.dialogYearPicker }
    private val monthPicker by lazy { binding.dialogMonthPicker }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context).create()

        yearPicker.minValue = 0
        yearPicker.maxValue = TodayCalendarUtils.year - 2022
        yearPicker.value =
            (viewModel.calendarYear.value ?: 2022) - 2022
        yearPicker.displayedValues =
            getDisplayedValues(yearPicker.maxValue + 1, 2022, false)

        setMonthPicker(yearPicker.value)
        monthPicker.value =
            (viewModel.calendarMonth.value ?: 1) - 1 - if (yearPicker.value == 0) 7 else 0

        yearPicker.setOnValueChangedListener { _, _, new ->
            setMonthPicker(new)
        }

        binding.calendarDialogSave.setOnClickListener {
            val yearValue = yearPicker.value + 2022
            val monthValue = monthPicker.value + 1 + if (yearValue == 2022) 7 else 0
            val isCurrentDate =
                TodayCalendarUtils.year == yearValue && TodayCalendarUtils.month == monthValue
            viewModel.changeCalendarDate(yearValue, monthValue, 1, !isCurrentDate)
            dialog.dismiss()
            dialog.cancel()
        }

        binding.calendarDialogCancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }

        dialog.setView(binding.root)
        dialog.create()
        return dialog
    }

    private fun setMonthPicker(yearValue: Int) {
        // 2023 -> 2022
        val minValue = if (yearValue == 0) 7 else 0
        monthPicker.displayedValues = null
        monthPicker.maxValue = when (yearValue) {
            0 -> 4
            yearPicker.maxValue -> TodayCalendarUtils.month - 1
            else -> 11
        }
        monthPicker.displayedValues =
            getDisplayedValues(monthPicker.maxValue + 1, minValue + 1)
        monthPicker.maxValue = monthPicker.displayedValues.size - 1
    }

    private fun getDisplayedValues(count: Int, min: Int, isMonth: Boolean = true) =
        Array(count) { "${min + it}${if (isMonth) "월" else "년"}" }


}