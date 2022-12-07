package com.hane24.hoursarenotenough24.overview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State

class OverViewFragment : Fragment() {
    lateinit var binding: FragmentOverviewBinding
    private val viewModel: OverViewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        registerRefreshBroadcastReceiver()
        observeErrorState()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate<FragmentOverviewBinding?>(
            inflater, R.layout.fragment_overview, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = this@OverViewFragment.viewModel
        }
    }

    private fun registerRefreshBroadcastReceiver() {
        activity?.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "REFRESH_CLICK" -> {
                        Log.i("refresh", "click event listen")
                        viewModel.refreshLogic()
                    }
                    else -> {
                        Log.i("refresh", "other event listen")
                    }
                }
            }
        }, IntentFilter("REFRESH_CLICK"))
    }

    private fun observeErrorState() {
        viewModel.state.observe(viewLifecycleOwner) { state: State? ->
            state?.let { handleError(it) }
        }
    }

    private fun handleError(state: State) =
        when (state) {
            State.SUCCESS -> Unit
            State.FAIL -> goToLogin(state)
            State.ERROR -> {
                val dialog = NetworkErrorDialog { dialog, id ->
                    viewModel.refreshLogic()
                }
                requireActivity().supportFragmentManager.let {
                    dialog.show(it, "error_dialog")
                }
            }
        }


    private fun goToLogin(state: State) {
        val intent = Intent(activity, LoginActivity::class.java)
            .putExtra("loginState", state)

        startActivity(intent).also { requireActivity().finish() }
    }
}