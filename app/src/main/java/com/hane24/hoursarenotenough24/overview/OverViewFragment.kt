package com.hane24.hoursarenotenough24.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding

class OverViewFragment : Fragment() {
    lateinit var binding: FragmentOverviewBinding
    private val viewModel by lazy { ViewModelProvider(this)[OverViewViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        setInitStateObserve()
        setinOutStateObserve()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setInitStateObserve() {
        viewModel.initState.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("intraId", viewModel.intraId.value)
                }
                startActivity(intent)
            }
        }
    }

    private fun setinOutStateObserve() {
        viewModel.inOutState.observe(viewLifecycleOwner) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("inOutState", viewModel.inOutState.value ?: false)
            }
            startActivity(intent)
        }
    }
}