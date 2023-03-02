package com.hane24.hoursarenotenough24.reissue

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentReissueBinding

class ReissueFragment : Fragment() {
    private lateinit var binding: FragmentReissueBinding
    private val viewModel: ReissueViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReissueBinding.inflate(layoutInflater, container, false)

        binding.reissueApplyButton.setOnClickListener { viewModel.clickReissueButton(requireActivity()) }
        return binding.root
    }
}