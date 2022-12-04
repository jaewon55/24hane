package com.hane24.hoursarenotenough24.utils

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.LogTableItem
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

@BindingAdapter("tableList")
fun bindTableRecyclerView(
    recyclerView: RecyclerView,
    data: List<LogTableItem>?
) {
    val adapter = recyclerView.adapter as LogTableAdapter
    adapter.submitList(data?.reversed())
}
