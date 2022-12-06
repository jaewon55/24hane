package com.hane24.hoursarenotenough24.inoutlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding

class LogListFragment : Fragment() {
    private lateinit var binding: FragmentLogListBinding
    private val viewModel: LogListViewModel by viewModels()

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
            LogCalendarAdapter(LogCalendarAdapter.OnClickListener { viewModel.changeSelectedDay(it) })
    }

}