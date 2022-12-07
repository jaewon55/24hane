package com.hane24.hoursarenotenough24.inoutlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding
import com.hane24.hoursarenotenough24.databinding.FragmentLogListCalendarItemBinding

class LogListFragment : Fragment() {
    private lateinit var binding: FragmentLogListBinding
    private val viewModel: LogListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setRecyclerAdapter()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate<FragmentLogListBinding?>(
            inflater,
            R.layout.fragment_log_list,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = this@LogListFragment.viewModel
        }
    }

    private fun setRecyclerAdapter() {
        binding.tableRecycler.adapter = LogTableAdapter()
        binding.calendarRecycler.layoutManager = object : GridLayoutManager(context, 7) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.calendarRecycler.adapter =
            LogCalendarAdapter(object : DiffUtil.ItemCallback<CalendarItem>() {
                override fun areItemsTheSame(
                    oldItem: CalendarItem,
                    newItem: CalendarItem
                ): Boolean {
                    return oldItem.day == newItem.day
                }

                override fun areContentsTheSame(
                    oldItem: CalendarItem,
                    newItem: CalendarItem
                ): Boolean {
                    return oldItem.day == newItem.day && oldItem.color == newItem.color
                }
            })
    }

    inner class LogCalendarAdapter(
        diffUtil: DiffUtil.ItemCallback<CalendarItem>
    ) :
        ListAdapter<CalendarItem, LogCalendarAdapter.LogCalendarViewHolder>(diffUtil) {

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
            holder.bind(item)
        }

        inner class LogCalendarViewHolder(private val itemBinding: FragmentLogListCalendarItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(item: CalendarItem) {
                itemBinding.item = item
                itemBinding.lifecycleOwner = viewLifecycleOwner
                itemBinding.viewModel = viewModel
            }
        }

    }

//        class OnClickListener(val clickListener: (day: Int) -> Unit) {
//            fun onClick(day: Int) = clickListener(day)
//        }

//        private fun checkNextDay(
//            button: MaterialButton,
//            item: CalendarItem,
//            onClickListener: LogCalendarAdapter.OnClickListener
//        ) {
//            button.isEnabled = !item.isNextDay
//            if (!button.isEnabled) return
//            button.setOnClickListener {
//                onClickListener.onClick(item.day)
//                selectDay = item.day
//                setStroke(button)
//            }
//        }

//        private fun checkSelected(button: MaterialButton, item: CalendarItem) {
//            if (selectDay == item.day) return setStroke(button)
//            button.apply {
//                strokeWidth = 1
//                strokeColor = ColorStateList.valueOf(
//                    getColorHelper(context, R.color.calendar_item_stroke_default)
//                )
//            }
//        }

//        private fun setStroke(newButton: MaterialButton) {
//            oldButton?.let {
//                it.strokeWidth = 1
//                it.strokeColor = ColorStateList.valueOf(
//                    getColorHelper(
//                        it.context,
//                        R.color.calendar_item_stroke_default
//                    )
//                )
//            }
//            newButton.let {
//                it.strokeWidth = 2
//                it.strokeColor = ColorStateList.valueOf(getColorHelper(it.context, R.color.red))
//            }
//            oldButton = newButton
//        }

//        companion object {
//            var oldButton: MaterialButton? = null
//            var selectDay: Int = TodayCalendarUtils.day
//        }


}