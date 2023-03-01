package com.hane24.hoursarenotenough24.overview

import android.animation.Animator
import android.animation.AnimatorInflater
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
import android.graphics.Rect
import android.os.Bundle
import android.text.method.Touch
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
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
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewGraphViewBinding
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import com.hane24.hoursarenotenough24.network.InOutTimeItem
import com.hane24.hoursarenotenough24.utils.SharedPreferenceUtils
import com.hane24.hoursarenotenough24.utils.bindDrawerClickable
import com.hane24.hoursarenotenough24.view.CustomProgressbar
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
        viewModel.monthProgressPercent.observe(requireActivity()) {
            progressChangeLogic(binding.overviewMonthProgressbar, it.toFloat())
        }
        viewModel.dayProgressPercent.observe(requireActivity()) {
            progressChangeLogic(binding.overviewTodayProgressbar, it.toFloat())
        }
        binding.overviewTodayCard.setOnClickListener { setCardAnimation(it) }
        binding.overviewMonthCard.setOnClickListener { setCardAnimation(it) }
        observeErrorState()

        Log.i("token", "${SharedPreferenceUtils.getAccessToken()}")
        return binding.root
    }

    private fun progressChangeLogic(view: CustomProgressbar, progress: Float) {
        view.maxProgress = progress
        progressAnimation(binding.overviewMonthProgressbar)
    }

    private fun measureCardHeight() {
        measureMinHeight()
        measureMaxHeight()
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
            if (view == binding.overviewTodayCard) {
                binding.overviewTodayBtn.animate().rotation(0f).apply { duration = 200 }
            } else {
                binding.overviewMonthBtn.animate().rotation(0f).apply { duration = 200 }
            }
            collapseAnimation(view)
        }
        else {
            if (view == binding.overviewTodayCard) {
                binding.overviewTodayBtn.animate().rotation(90f).apply { duration = 200 }
            } else {
                binding.overviewMonthBtn.animate().rotation(90f).apply { duration = 200 }
            }
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
            reverseVisibility(binding.overviewTodayToggleGroup)
        } else {
            reverseVisibility(binding.overviewMonthToggleGroup)
        }
    }

    private fun progressAnimation(view: CustomProgressbar) {
        if (view.maxProgress == 0f) {
            if (view == binding.overviewMonthProgressbar)
                binding.overviewMonthCard.isClickable = true
            else
                binding.overviewTodayCard.isClickable = true
            return
        }
        view.progress = 0f
        val progress = if (view.maxProgress > 100f) 100f else view.maxProgress
        val progressAnimation = ObjectAnimator.ofFloat(view, "progress", progress).apply {
            duration=1000
        }

        progressAnimation.doOnEnd {
            if (view == binding.overviewMonthProgressbar)
                binding.overviewMonthCard.isClickable = true
            else
                binding.overviewTodayCard.isClickable = true
        }

        progressAnimation.start()
    }
    private fun expandAnimation(view: View) {
        val expandAnimation = object: Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime >= 0.7f) {
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
                if (view == binding.overviewTodayCard)
                    progressAnimation(binding.overviewTodayProgressbar)
                else
                    progressAnimation(binding.overviewMonthProgressbar)
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
                    if (interpolatedTime >= 0.7f) minHeight else (maxHeight - (maxHeight * interpolatedTime)).toInt()
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
                if (view == binding.overviewTodayCard)
                    binding.overviewTodayProgressbar.progress = 0f
                else
                    binding.overviewMonthProgressbar.progress = 0f
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
            binding.overviewMonthHourText.setTextColor(Color.BLACK)
            binding.overviewMonthHourNumericText.setTextColor(Color.BLACK)
            binding.overviewMonthMinuteNumericText.setTextColor(Color.BLACK)
            binding.overviewMonthMinuteText.setTextColor(Color.BLACK)
            binding.overviewMonthBtn.imageTintList = ColorStateList.valueOf(Color.GRAY)
            binding.overviewMonthAccumulateText.setTextColor(Color.BLACK)
        } else {
            binding.overviewMonthCard.backgroundTintList = ColorStateList.valueOf(Color.argb(255, 115, 91, 242))
            binding.overviewMonthHourText.setTextColor(Color.WHITE)
            binding.overviewMonthHourNumericText.setTextColor(Color.WHITE)
            binding.overviewMonthMinuteNumericText.setTextColor(Color.WHITE)
            binding.overviewMonthMinuteText.setTextColor(Color.WHITE)
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
}