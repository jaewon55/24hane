package com.hane24.hoursarenotenough24.inoutlog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.databinding.FragmentLogListCalendarItemBinding

class LogCalendarAdapter :
    ListAdapter<CalendarItem, LogCalendarAdapter.LogCalendarViewHolder>(DiffCallback) {

    class LogCalendarViewHolder(private val binding: FragmentLogListCalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogCalendarViewHolder =
        LogCalendarViewHolder(
            FragmentLogListCalendarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: LogCalendarViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CalendarItem>() {
        override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.color == newItem.color && oldItem.day == newItem.day
        }
    }
}