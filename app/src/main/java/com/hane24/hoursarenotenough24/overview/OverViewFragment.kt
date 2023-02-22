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
            holder.bind(items[position])

            if (position == items.size-1)
                viewPager.post(runnable)
        }

        override fun getItemCount(): Int {
            return 2
        }

        class TouchDelegateHelper(view: View): TouchDelegate(Rect(), view) {
            private val delegates = mutableListOf<TouchDelegate>()
            fun addDelegate(new: TouchDelegate) {
                delegates += new
            }
            override fun onTouchEvent(event: MotionEvent): Boolean {
                var res = false
                delegates.forEach {
                    event.setLocation(event.x, event.y)
                    res = it.onTouchEvent(event) || res
                }
                return res
            }
        }

        class GraphViewHolder private constructor (val binding: FragmentOverviewGraphViewBinding) : RecyclerView.ViewHolder(binding.root) {
            private val graphs = arrayOf(
                binding.overviewGraph1,
                binding.overviewGraph2,
                binding.overviewGraph3,
                binding.overviewGraph4,
                binding.overviewGraph5,
                binding.overviewGraph6
            )

            private val selectors = arrayOf(
                binding.overviewGraphTriangle1,
                binding.overviewGraphTriangle2,
                binding.overviewGraphTriangle3,
                binding.overviewGraphTriangle4,
                binding.overviewGraphTriangle5,
                binding.overviewGraphTriangle6
                )

            private var currSelect = 0
            private fun getGraphHeight(percent: Long): Int {
                val maxHeight = 87
                val whiteHeight = (maxHeight * (percent * 0.01)).toInt()
                val density = App.instance.applicationContext.resources.displayMetrics.density
                return (whiteHeight * density).toInt()
            }

            private fun spanClickArea() {
                val parent = binding.constraintLayout

                val touchDelegateHelper = TouchDelegateHelper(parent)
                val density = App.instance.applicationContext.resources.displayMetrics.density

                parent.post {
                    graphs.forEach {
                        val hitRect = Rect()
                        it.getHitRect(hitRect)
                        hitRect.top = (87 * density).toInt()

                        touchDelegateHelper.addDelegate(TouchDelegate(hitRect, it))
                    }
                    parent.touchDelegate = touchDelegateHelper
                }
            }

            private fun graphClickLogic(index: Int) {
                val graphScaleAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.scale_anim)
                val selectorOpenAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.tanslate_anim)
                val selectorCloseAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.reverse_translate_anim)
                graphScaleAnim.interpolator = LinearInterpolator()
                selectorOpenAnim.interpolator = LinearInterpolator()
                selectorCloseAnim.interpolator = LinearInterpolator()
                selectorCloseAnim.setAnimationListener(object: AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        selectors[currSelect].visibility = View.GONE
                        selectors[index].visibility = View.VISIBLE
                        selectors[index].startAnimation(selectorOpenAnim)
                        currSelect = index
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}
                })
                selectorOpenAnim.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        graphs.forEach { it.isClickable = true }
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}
                })
                graphs[index].startAnimation(graphScaleAnim)
                if (currSelect != index) {
                    graphs.forEach {
                        it.isClickable = false
                    }
                    selectors[currSelect].startAnimation(selectorCloseAnim)
                }
            }
            fun bind(item: TimeInfo) {
                binding.overviewGraphName.text =
                    if (item.timeType == 0) "최근 주간 그래프" else "최근 월간 그래프"
                binding.overviewGraphType.text = if (item.timeType == 0) "(6주)" else "(6달)"
                spanClickArea()
                for ((idx, graph) in graphs.withIndex()) {
                    graph.layoutParams.height = getGraphHeight(item.accumulationTimes[idx])
                    graph.setOnClickListener { graphClickLogic(idx) }
                }
            }
            companion object {
                fun from(parent: ViewGroup): GraphViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = FragmentOverviewGraphViewBinding.inflate(layoutInflater, parent, false)
                    return GraphViewHolder(binding)
                }
            }
        }
    }
}