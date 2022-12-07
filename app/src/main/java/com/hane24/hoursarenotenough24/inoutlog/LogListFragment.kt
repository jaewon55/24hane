package com.hane24.hoursarenotenough24.inoutlog

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State

class LogListFragment : Fragment() {
    private lateinit var binding: FragmentLogListBinding
    private val viewModel by lazy { ViewModelProvider(this)[LogListViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        initBinding(inflater, container)
        registerRefreshBroadcastReceiver()
        observeErrorState()
        setRecyclerAdapter()
        return binding.root
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_log_list, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
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
        viewModel.errorState.observe(viewLifecycleOwner) { state: State? ->
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

    private fun setRecyclerAdapter() {
        binding.tableRecycler.adapter = LogTableAdapter()
        binding.calendarRecycler.layoutManager = object : GridLayoutManager(context, 7) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.calendarRecycler.adapter =
            LogCalendarAdapter(LogCalendarAdapter.OnClickListener { viewModel.changeSelectedDay(it) })
    }
}