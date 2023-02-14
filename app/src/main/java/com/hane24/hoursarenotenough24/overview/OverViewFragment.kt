package com.hane24.hoursarenotenough24.overview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.hane24.hoursarenotenough24.MainActivity

import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewBinding
import com.hane24.hoursarenotenough24.error.UnknownServerErrorDialog
import com.hane24.hoursarenotenough24.login.LoginActivity
import com.hane24.hoursarenotenough24.login.State
import kotlin.math.min

class OverViewFragment : Fragment() {
    private lateinit var binding: FragmentOverviewBinding
    private var minHeight = Int.MIN_VALUE
    private var maxHeight = Int.MIN_VALUE
    private val viewModel: OverViewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initBinding(inflater, container)

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
}