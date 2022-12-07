package com.hane24.hoursarenotenough24.inoutlog

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.databinding.FragmentLogListCalendarItemBinding
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import com.hane24.hoursarenotenough24.utils.getColorHelper

class LogCalendarAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<CalendarItem, LogCalendarAdapter.LogCalendarViewHolder>(diffUtil) {

    class LogCalendarViewHolder(private val binding: FragmentLogListCalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem, onClickListener: OnClickListener) {
            binding.item = item
            checkNextDay(binding.calendarItem, item, onClickListener)
            checkSelected(binding.calendarItem, item)
        }

        private fun checkNextDay(
            button: MaterialButton,
            item: CalendarItem,
            onClickListener: OnClickListener
        ) {
            button.isEnabled = !item.isNextDay
            if (!button.isEnabled) return
            button.setOnClickListener {
                onClickListener.onClick(item.day)
                selectDay = item.day
                setStroke(button)
            }
        }

        private fun checkSelected(button: MaterialButton, item: CalendarItem) {
            if (selectDay == item.day) return setStroke(button)
            button.apply {
                strokeWidth = 1
                strokeColor = ColorStateList.valueOf(
                    getColorHelper(context, R.color.calendar_item_stroke_default)
                )
            }
        }

        private fun setStroke(newButton: MaterialButton) {
            oldButton?.let {
                it.strokeWidth = 1
                it.strokeColor = ColorStateList.valueOf(
                    getColorHelper(
                        it.context,
                        R.color.calendar_item_stroke_default
                    )
                )
            }
            newButton.let {
                it.strokeWidth = 2
                it.strokeColor = ColorStateList.valueOf(getColorHelper(it.context, R.color.red))
            }
            oldButton = newButton
        }

        companion object {
            var oldButton: MaterialButton? = null
            var selectDay: Int = TodayCalendarUtils.day
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogCalendarViewHolder {
        return LogCalendarViewHolder(
            FragmentLogListCalendarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: LogCalendarViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClickListener)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CalendarItem>() {
            override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
                return oldItem.day == newItem.day && oldItem.color == newItem.color
            }
        }
    }

    class OnClickListener(val clickListener: (day: Int) -> Unit) {
        fun onClick(day: Int) = clickListener(day)
    }
}