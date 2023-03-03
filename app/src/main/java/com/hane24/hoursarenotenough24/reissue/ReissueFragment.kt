package com.hane24.hoursarenotenough24.reissue

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.hane24.hoursarenotenough24.MainActivity

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding
import com.hane24.hoursarenotenough24.databinding.FragmentReissueBinding
import com.hane24.hoursarenotenough24.etcoption.EtcOptionFragment

class ReissueFragment : Fragment() {
    private lateinit var binding: FragmentReissueBinding
    private val viewModel: ReissueViewModel by activityViewModels()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            (activity as MainActivity).moveToFragment(EtcOptionFragment())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        initBinding(inflater, container)

        binding.reissueBackButton.setOnClickListener { backPressedCallback.handleOnBackPressed() }

        binding.reissueApplyButton.setOnClickListener { viewModel.clickReissueButton(requireActivity()) }
        return binding.root
    }


    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reissue, container, false)
        binding.let {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = this.viewModel
        }
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }
}