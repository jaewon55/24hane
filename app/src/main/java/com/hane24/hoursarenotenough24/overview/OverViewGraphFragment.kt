package com.hane24.hoursarenotenough24.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverViewGraphBinding
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding

class OverViewGraphFragment : Fragment() {
    private lateinit var binding: FragmentOverViewGraphBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverViewGraphBinding.inflate(layoutInflater, container, false)

        val type = arguments?.getInt("data")

        type?.also {
            val name = when (it) {
                0 -> "최근 주간 그래프"
                else -> "최근 월간 그래프"
            }
            val type = when (it) {
                0 -> "(6주)"
                else -> "(6달)"
            }
            binding.overviewGraphName.text = name
            binding.overviewGraphType.text = type
        }
        return binding.root
    }
}