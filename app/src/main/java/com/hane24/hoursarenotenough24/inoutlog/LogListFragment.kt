package com.hane24.hoursarenotenough24.inoutlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding

class LogListFragment : Fragment() {
    private lateinit var binding: FragmentLogListBinding
    private val viewModel by lazy { ViewModelProvider(this)[LogListViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        setRecyclerAdapter()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_list, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setRecyclerAdapter() {
        binding.tableRecycler.adapter = LogTableAdapter()
    }

}