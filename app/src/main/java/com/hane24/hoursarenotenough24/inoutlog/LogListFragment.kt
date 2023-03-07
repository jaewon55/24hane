package com.hane24.hoursarenotenough24.inoutlog

import android.content.Intent
import android.graphics.Rect
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.hane24.hoursarenotenough24.MainActivity
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.data.CalendarItem
import com.hane24.hoursarenotenough24.databinding.FragmentLogListBinding
import com.hane24.hoursarenotenough24.databinding.FragmentLogListCalendarItemBinding
import com.hane24.hoursarenotenough24.error.NetworkErrorDialog
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.overview.OverViewViewModel
import com.hane24.hoursarenotenough24.overview.TimeDialogFragment

class LogListFragment : Fragment() {
    private lateinit var binding: FragmentLogListBinding
    private val viewModel: LogListViewModel by activityViewModels()
    private val overViewViewModel: OverViewViewModel by activityViewModels()
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
        observeInOutState()
        setCalendarDateOnClick()
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

    private fun observeErrorState() {
        viewModel.errorState.observe(viewLifecycleOwner) { state: State? ->
            state?.let { handleError(it) }
        }
    }

    private fun handleError(state: State) =
        when (state) {
            State.LOGIN_FAIL -> goToLogin(state)
            State.UNKNOWN_ERROR -> UnknownServerErrorDialog.showUnknownServerErrorDialog(
                requireActivity().supportFragmentManager
            )
            State.NETWORK_FAIL -> NetworkErrorDialog.showNetworkErrorDialog(
                requireActivity().supportFragmentManager
            ) { _, _ -> viewModel.refreshButtonOnClick() }
            else -> Unit
        }

    private fun observeInOutState() {
        overViewViewModel.inOutState.observe(viewLifecycleOwner) {
            viewModel.setInOutState(it)
        }
    }

    private fun goToLogin(state: State) {
        val intent = Intent(activity, LoginActivity::class.java)
            .putExtra("loginState", state)

        startActivity(intent).also { requireActivity().finish() }
    }

    private fun setCalendarDateOnClick() {
        binding.calendarDate.setOnClickListener {
            val newDialog = CalendarDialog()

            activity?.supportFragmentManager?.let {
                newDialog.show(it, "timeDialog")
            }
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
                    return oldItem.day < 0
                }

                override fun areContentsTheSame(
                    oldItem: CalendarItem,
                    newItem: CalendarItem
                ): Boolean {
                    return oldItem.day == newItem.day
                }
            })
        binding.calendarRecycler.itemAnimator = null
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
                itemBinding.buttonParent.post {
                    val delegateArea = Rect()
                    val button = itemBinding.calendarItem.apply { getHitRect(delegateArea) }
                    delegateArea.right += 30
                    delegateArea.left -= 30
                    delegateArea.top -= 30
                    delegateArea.bottom += 30
                    (button.parent as? View)?.apply {
                        touchDelegate = TouchDelegate(delegateArea, button)
                    }
                }
            }
        }

    }
}