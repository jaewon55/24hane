package com.hane24.hoursarenotenough24.reissue

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.etcoption.EtcOptionFragment
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.BASE_URL

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
        observeErrorState()
        setHelpButtonOnClick()
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

    private fun observeErrorState() {
        viewModel.errorState.observe(viewLifecycleOwner) { state: State? ->
            state?.let { handleError(it) }
        }
    }

    private fun handleError(state: State) =
        when (state) {
            State.UNKNOWN_ERROR, State.SERVER_FAIL -> UnknownServerErrorDialog.showUnknownServerErrorDialog(requireActivity().supportFragmentManager)
            State.NETWORK_FAIL -> NetworkErrorDialog.showNetworkErrorDialog(
                requireActivity().supportFragmentManager
            ) { _, _ -> viewModel.refreshButtonOnClick() }
            else -> {}
        }
    private fun setHelpButtonOnClick() {
        binding.reissueHelpButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL + "redirect/reissuance_guidelines"))
            startActivity(intent)
        }
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }
}