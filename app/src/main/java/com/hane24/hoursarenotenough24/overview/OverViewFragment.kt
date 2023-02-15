package com.hane24.hoursarenotenough24.overview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.MainActivity

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding
import com.hane24.hoursarenotenough24.databinding.OverviewGraphViewBinding
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import kotlin.math.min

class OverViewFragment : Fragment() {
    private lateinit var binding: FragmentOverviewBinding
    private val pager by lazy { binding.overviewGraphViewpager }
    private var minHeight = Int.MIN_VALUE
    private var maxHeight = Int.MIN_VALUE
    private val viewModel: OverViewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        initViewPager()
        binding.overviewTodayCard.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        minHeight = binding.overviewTodayCard.measuredHeight
        Log.i("data", "${binding.overviewTodayCard.measuredHeight}")


        binding.overviewTodayCard.setOnClickListener {
            if (!isMeasuredMaxHeight() || !isChildViewVisible(it))
                slideCardView(it)
            else {
                if (isToggled(it)) {
                    binding.overviewTodayBtn.animate().rotation(0f).apply { duration = 200 }
                    collapseAnimation(it)
                }
                else {
                    binding.overviewTodayBtn.animate().rotation(90f).apply { duration = 200 }
                    expandAnimation(it)
                }
            }
        }

        binding.overviewMonthCard.setOnClickListener {
            if (!isMeasuredMaxHeight() || !isChildViewVisible(it))
                slideCardView(it)
            else {
                if (isToggled(it)) {
                    binding.overviewMonthBtn.animate().rotation(0f).apply { duration = 200 }
                    collapseAnimation(it)
                }
                else {
                    binding.overviewMonthBtn.animate().rotation(90f).apply { duration = 200 }
                    expandAnimation(it)
                }
            }
        }

        observeErrorState()

        return binding.root
    }

    private fun initViewPager() {
        val monthlyTimeInfo = TimeInfo(listOf(20L, 30L, 40L, 50L, 60L, 100L), 1)
        val weeklyTimeInfo = TimeInfo(listOf(60L, 100L, 40L, 30L, 20L, 10L), 0)
        val adapter = GraphViewPagerAdapter(listOf(weeklyTimeInfo, monthlyTimeInfo), pager)

        pager.adapter = adapter
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun isChildViewVisible(view: View): Boolean {
        if (view == binding.overviewTodayCard) {
            return binding.overviewTodayTargetText.visibility == View.VISIBLE
        } else if (view == binding.overviewMonthCard) {
            return binding.overviewMonthTargetText.visibility == View.VISIBLE
        }
        return false
    }

    private fun isMeasuredMaxHeight() = maxHeight != Int.MIN_VALUE

    private fun isToggled(view: View) = view.height == maxHeight
    private fun expandAnimation(view: View) {
        val expandAnimation = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime >= 0.8f) maxHeight else (minHeight + (minHeight * interpolatedTime)).toInt()
                view.requestLayout()
            }
        }.apply { duration = 200L }
        view.startAnimation(expandAnimation)
    }

    private fun collapseAnimation(view: View) {
        val collapseAnimation = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime >= 0.8f) minHeight else (maxHeight - (maxHeight * interpolatedTime)).toInt()
                view.requestLayout()
            }
        }.apply { duration = 200L }
        view.startAnimation(collapseAnimation)
    }

    fun slideCardView(view: View) {
        Log.i("data", "minHeight: $minHeight maxHeight: $maxHeight")

        if (view == binding.overviewTodayCard) {
                binding.overviewTodayBtn.animate().setDuration(200).rotation(90f)
                binding.overviewTodayTargetText.visibility = View.VISIBLE
                binding.overviewTodayTargetTimeText.visibility = View.VISIBLE
                binding.overviewTodayProgressbar.visibility = View.VISIBLE
                binding.overviewTodayCard.measure(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                maxHeight = binding.overviewTodayCard.measuredHeight
            Log.i("data", "after: ${binding.overviewTodayLayout.measuredHeight}")
        } else if (view == binding.overviewMonthCard) {
            binding.overviewMonthBtn.animate().setDuration(200).rotation(90f)
            binding.overviewMonthTargetText.visibility = View.VISIBLE
            binding.overviewMonthTargetTimeText.visibility = View.VISIBLE
            binding.overviewMonthProgressbar.visibility = View.VISIBLE
            binding.overviewMonthCard.measure(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            maxHeight = binding.overviewMonthCard.measuredHeight
        }
    }

    fun targetTimeOnClick(isMonth: Boolean) {
        val newDialog = TimeDialogFragment(isMonth)

        activity?.supportFragmentManager?.let {
            newDialog.show(it, "timeDialog")
        }
    }

    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate<FragmentOverviewBinding?>(
            inflater, R.layout.fragment_overview, container, false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = this@OverViewFragment.viewModel
            this.fragment = this@OverViewFragment
        }
    }

    private fun observeErrorState() {
        viewModel.state.observe(viewLifecycleOwner) { state: State? ->
            state?.let { handleError(it) }
        }
    }

    private fun handleError(state: State) =
        when (state) {
            State.LOGIN_FAIL -> goToLogin(state)
            State.UNKNOWN_ERROR -> UnknownServerErrorDialog.showUnknownServerErrorDialog(requireActivity().supportFragmentManager)
            else -> {}
        }

    private fun goToLogin(state: State) {
        val intent = Intent(activity, LoginActivity::class.java)
            .putExtra("loginState", state)

        startActivity(intent).also { requireActivity().finish() }
    }

    data class TimeInfo(val accumulationTimes: List<Long>, val timeType: Int)
    class GraphViewPagerAdapter(
        private val items: List<TimeInfo>,
        private val viewPager: ViewPager2
        ): RecyclerView.Adapter<GraphViewPagerAdapter.GraphViewHolder>() {
        private val runnable = Runnable { items }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphViewHolder {
            return GraphViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: GraphViewHolder, position: Int) {
            holder.bind(items[position], viewPager)

            if (position == items.size-1)
                viewPager.post(runnable)
        }

        override fun getItemCount(): Int {
            return 2
        }

        class GraphViewHolder private constructor (val binding: OverviewGraphViewBinding) : RecyclerView.ViewHolder(binding.root) {
            fun getGraphHeight(percent: Long): Int {
                val maxHeight = 87
                val whiteHeight = maxHeight - (maxHeight * (percent * 0.01)).toInt()
                val density = App.instance.applicationContext.resources.displayMetrics.density
                return (whiteHeight * density).toInt()
            }
            fun bind(item: TimeInfo, viewPager: ViewPager2) {
                val maxHeight = 87
                TabLayoutMediator(binding.overviewGraphTab, viewPager) { _,_ -> }.attach()
                binding.overviewGraphName.text = if (item.timeType == 0) "최근 주간 그래프" else "최근 월간 그래프"
                binding.overviewGraphType.text = if (item.timeType == 0) "(6주)" else "(6달)"
                binding.overviewWhiteGraph1.layoutParams.height = getGraphHeight(item.accumulationTimes[0])
                binding.overviewWhiteGraph2.layoutParams.height = getGraphHeight(item.accumulationTimes[1])
                binding.overviewWhiteGraph3.layoutParams.height = getGraphHeight(item.accumulationTimes[2])
                binding.overviewWhiteGraph4.layoutParams.height = getGraphHeight(item.accumulationTimes[3])
                binding.overviewWhiteGraph5.layoutParams.height = getGraphHeight(item.accumulationTimes[4])
                binding.overviewWhiteGraph6.layoutParams.height = getGraphHeight(item.accumulationTimes[5])
            }
            companion object {
                fun from(parent: ViewGroup): GraphViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = OverviewGraphViewBinding.inflate(layoutInflater, parent, false)
                    return GraphViewHolder(binding)
                }
            }
        }
    }
}