package com.hane24.hoursarenotenough24.overview

import android.animation.ObjectAnimator
import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.Transformation
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.TransitionInflater
import com.google.android.material.tabs.TabLayoutMediator

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.notification.NotificationFragment
import com.hane24.hoursarenotenough24.view.CustomProgressbar
import kotlinx.coroutines.launch

class OverViewFragment : Fragment() {
    private lateinit var binding: FragmentOverviewBinding
    private val pager by lazy { binding.overviewGraphViewpager }
    private var minHeight = Int.MIN_VALUE
    private var maxHeight = Int.MIN_VALUE
    private val viewModel: OverViewViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)
        initViewPager()
        measureCardHeight()
        binding.overviewProfileImage.clipToOutline = true
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthProgressPercent.collect {
                    progressChangeLogic(binding.overviewMonthProgressbar, it.toFloat())
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayProgressPercent.collect {
                    progressChangeLogic(binding.overviewTodayProgressbar, it.toFloat())
                }
            }
        }

        binding.overviewTodayCard.setOnClickListener { setCardAnimation(it) }
        binding.overviewMonthCard.setOnClickListener { setCardAnimation(it) }
        binding.overviewAnnounceImage.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, NotificationFragment())
                .commit()
        }
        return binding.root
    }

    private fun progressChangeLogic(view: CustomProgressbar, progress: Float) {
        view.maxProgress = progress
        progressAnimation(view)
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
        binding.overviewTodayCard.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        maxHeight = binding.overviewTodayCard.measuredHeight
        reverseViewVisibility(binding.overviewTodayCard)
    }
    private fun initViewPager() {
        val adapter = GraphViewPagerAdapter(viewModel.accumulationGraphInfo.value)

        pager.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accumulationGraphInfo.collect {
                    it.let {
                        adapter.setItem(it)
                    }
                }
            }
        }
        TabLayoutMediator(binding.overviewGraphTab, pager) { _,_ -> }.attach()
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

//    private fun observeErrorState() {
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.state.collect {
//                    it?.let { handleError(it) }
//                }
//            }
//        }
//    }

//    private fun handleError(state: State) =
//        when (state) {
//            State.LOGIN_FAIL -> goToLogin()
//            State.UNKNOWN_ERROR -> ErrorDialog.show(requireActivity().supportFragmentManager)
//            State.NETWORK_FAIL -> NetworkErrorDialog.show(
//                requireActivity().supportFragmentManager
//            ) { _, _ -> viewModel.refreshButtonOnClick() }
//            else -> {}
//    }

    private fun goToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)

        startActivity(intent).also { requireActivity().finish() }
    }

}