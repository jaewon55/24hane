package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.data.LogTableItem
import com.hane24.hoursarenotenough24.inoutlog.LogCalendarAdapter
import com.hane24.hoursarenotenough24.inoutlog.LogTableAdapter

fun getColorHelper(context: Context, id: Int) =
    if (Build.VERSION.SDK_INT >= 23) context.getColor(id) else context.resources.getColor(id)

@BindingAdapter(value = ["isStateOn"], requireAll = false)
fun ProgressBar.isStateOn(state: Boolean) {
    if (state) {
        progressTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.on_progress_front))
        progressBackgroundTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.on_progress_back))
    } else {
        progressTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.off_progress_front))
        progressBackgroundTintList =
            ColorStateList.valueOf(getColorHelper(context, R.color.off_progress_back))
    }
}

@BindingAdapter("buttonState")
fun bindLeftButtonState(
    button: ImageButton,
    state: Boolean
) {
    button.isEnabled = state
}

@BindingAdapter("tableList")
fun bindTableRecyclerView(
    recyclerView: RecyclerView,
    data: List<LogTableItem>?
) {
    recyclerView.visibility = if (data?.isEmpty() != false) View.INVISIBLE else View.VISIBLE
    val adapter = recyclerView.adapter as LogTableAdapter
    adapter.submitList(data?.reversed())
}

@BindingAdapter("tableList")
fun bindTableTextView(
    textView: TextView,
    data: List<LogTableItem>?
) {
    textView.visibility = if (data?.isEmpty() != false) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("calendarGrid")
fun bindCalendarRecyclerView(
    recyclerView: RecyclerView,
    data: List<CalendarItem>?
) {
    val adapter = recyclerView.adapter as LogCalendarAdapter
    adapter.submitList(data)
}

@BindingAdapter("item")
fun setCalendarItem(
    button: MaterialButton,
    item: CalendarItem,
) {
    button.text = item.day.toString()
    button.backgroundTintList = ColorStateList.valueOf(getColorHelper(button.context, item.color))
    if (item.isNextDay) {
        button.setTextColor(getColorHelper(button.context, R.color.next_day_text))
    } else {
        button.setTextColor(getColorHelper(button.context, R.color.black))
    }
}

@BindingAdapter("loadingState")
fun setCalendarVisible(
    recyclerView: RecyclerView,
    loadingState: Boolean
) {
    recyclerView.visibility = if (loadingState) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("loadingState")
fun setProgressBarVisible(
    progressBar: ProgressBar,
    loadingState: Boolean
) {
    progressBar.visibility = if (loadingState) View.VISIBLE else View.INVISIBLE
}

