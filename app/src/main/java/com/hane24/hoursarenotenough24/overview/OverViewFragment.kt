package com.hane24.hoursarenotenough24.overview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.LayoutTransition.TransitionListener
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
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
import androidx.room.Room
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.MainActivity

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.database.TimeDatabase
import com.hane24.hoursarenotenough24.database.TimeDatabaseDto
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding
import com.hane24.hoursarenotenough24.databinding.OverviewGraphViewBinding
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.InOutTimeItem
import com.hane24.hoursarenotenough24.utils.bindDrawerClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        measureCardHeight()
        binding.overviewTodayCard.setOnClickListener { setCardAnimation(it) }
        binding.overviewMonthCard.setOnClickListener { setCardAnimation(it) }
        observeErrorState()
        return binding.root
    }

    private fun measureCardHeight() {
        measureMinHeight()
        measureMaxHeight()
        binding.loadingLayout.visibility = View.GONE
    }

    private fun measureMinHeight() {
        binding.overviewTodayCard.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        minHeight = binding.overviewTodayCard.measuredHeight
    }

    private fun measureMaxHeight() {
        reverseViewVisibility(binding.overviewTodayCard)
        binding.overviewTodayCard.measure(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        maxHeight = binding.overviewTodayCard.measuredHeight
        reverseViewVisibility(binding.overviewTodayCard)
    }
    private fun initViewPager() {
        val monthlyTimeInfo = TimeInfo(listOf(20L, 30L, 40L, 50L, 60L, 100L), 1)
        val weeklyTimeInfo = TimeInfo(listOf(60L, 100L, 40L, 30L, 20L, 10L), 0)
        val adapter = GraphViewPagerAdapter(listOf(weeklyTimeInfo, monthlyTimeInfo), pager)

        pager.adapter = adapter
        TabLayoutMediator(binding.overviewGraphTab, pager) { _,_ -> }.attach()
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

    private fun setCardAnimation(view: View) {
        view.isClickable = false
        if (isToggled(view)) {
            binding.overviewTodayBtn.animate().rotation(0f).apply { duration = 200 }
            collapseAnimation(view)
        }
        else {
            binding.overviewTodayBtn.animate().rotation(90f).apply { duration = 200 }
            expandAnimation(view)
        }
    }
    private fun isToggled(view: View) = view.height == maxHeight

    private fun reverseViewVisibility(view: View) {
        val reverseVisibility = {  view: View ->
            view.visibility = if (view.visibility == View.GONE)
                View.VISIBLE
            else
                View.GONE
        }
        if (view == binding.overviewTodayCard) {
            reverseVisibility(binding.overviewTodayTargetText)
            reverseVisibility(binding.overviewTodayTargetTimeText)
            reverseVisibility(binding.overviewTodayProgressbar)
            reverseVisibility(binding.overviewTodayProgressbarPercent)
            reverseVisibility(binding.overviewTodayProgressbarTarget)
            reverseVisibility(binding.overviewTodayProgressbarTargetTime)
        } else {
            reverseVisibility(binding.overviewMonthTargetText)
            reverseVisibility(binding.overviewMonthTargetTimeText)
            reverseVisibility(binding.overviewMonthProgressbar)
            reverseVisibility(binding.overviewMonthProgressbarPercent)
            reverseVisibility(binding.overviewMonthProgressbarTarget)
            reverseVisibility(binding.overviewMonthProgressbarTargetTime)
        }
    }
    private fun expandAnimation(view: View) {
        val expandAnimation = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime >= 0.8f) {
                        maxHeight
                    } else (minHeight + (minHeight * interpolatedTime)).toInt()
                view.requestLayout()
            }
        }.apply { duration = 200L }

        val animListener = object: AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                reverseMonthCardColor(view)
            }

            override fun onAnimationEnd(p0: Animation?) {
                reverseViewVisibility(view)
                view.isClickable = true
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        }

        expandAnimation.setAnimationListener(animListener)
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

        val animListener = object: AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                reverseViewVisibility(view)
            }
            override fun onAnimationEnd(p0: Animation?) {
                reverseMonthCardColor(view)
                view.isClickable = true
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        }

        collapseAnimation.setAnimationListener(animListener)
        view.startAnimation(collapseAnimation)
    }
    private fun reverseMonthCardColor(view: View) {
        if (view != binding.overviewMonthCard) return
        if (binding.overviewMonthAccumulateText.textColors == ColorStateList.valueOf(Color.WHITE)) {
            binding.overviewMonthCard.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            binding.overviewMonthAccumulateTimeText.setTextColor(Color.BLACK)
            binding.overviewMonthBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
            binding.overviewMonthAccumulateText.setTextColor(Color.BLACK)
        } else {
            binding.overviewMonthCard.backgroundTintList = ColorStateList.valueOf(Color.argb(255, 115, 91, 242))
            binding.overviewMonthAccumulateTimeText.setTextColor(Color.WHITE)
            binding.overviewMonthBtn.imageTintList = ColorStateList.valueOf(Color.WHITE)
            binding.overviewMonthAccumulateText.setTextColor(Color.WHITE)
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
                val whiteHeight = (maxHeight * (percent * 0.01)).toInt()
                val density = App.instance.applicationContext.resources.displayMetrics.density
                return (whiteHeight * density).toInt()
            }
            fun bind(item: TimeInfo, viewPager: ViewPager2) {
                binding.overviewGraphName.text = if (item.timeType == 0) "최근 주간 그래프" else "최근 월간 그래프"
                binding.overviewGraphType.text = if (item.timeType == 0) "(6주)" else "(6달)"
                binding.overviewGraph1.layoutParams.height = getGraphHeight(item.accumulationTimes[0])
                binding.overviewGraph2.layoutParams.height = getGraphHeight(item.accumulationTimes[1])
                binding.overviewGraph3.layoutParams.height = getGraphHeight(item.accumulationTimes[2])
                binding.overviewGraph4.layoutParams.height = getGraphHeight(item.accumulationTimes[3])
                binding.overviewGraph5.layoutParams.height = getGraphHeight(item.accumulationTimes[4])
                binding.overviewGraph6.layoutParams.height = getGraphHeight(item.accumulationTimes[5])
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