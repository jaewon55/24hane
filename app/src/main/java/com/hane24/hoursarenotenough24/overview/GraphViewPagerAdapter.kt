package com.hane24.hoursarenotenough24.overview

import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewGraphViewBinding

class GraphViewPagerAdapter(
    private val viewPager: ViewPager2
): RecyclerView.Adapter<GraphViewPagerAdapter.GraphViewHolder>() {
    private var items: List<OverViewFragment.TimeInfo> =
        listOf(
            OverViewFragment.TimeInfo(listOf(10L, 0L, 0L, 0L, 0L, 0L), 0),
            OverViewFragment.TimeInfo(listOf(0L, 0L, 0L, 0L, 0L, 0L), 1)
        )
    private val runnable = Runnable { items }
    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphViewHolder {
        return GraphViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GraphViewHolder, position: Int) {
        holder.bind(items[position])

        if (position == items.size-1)
            viewPager.post(runnable)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItem(item: List<OverViewFragment.TimeInfo>) {
        items = item
        notifyDataSetChanged()
    }

    class GraphViewHolder private constructor (val binding: FragmentOverviewGraphViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private val graphs by lazy {
            arrayOf(
                binding.overviewGraph1,
                binding.overviewGraph2,
                binding.overviewGraph3,
                binding.overviewGraph4,
                binding.overviewGraph5,
                binding.overviewGraph6
            )
        }

        private val selectors by lazy {
            arrayOf(
                binding.overviewGraphSelector1,
                binding.overviewGraphSelector2,
                binding.overviewGraphSelector3,
                binding.overviewGraphSelector4,
                binding.overviewGraphSelector5,
                binding.overviewGraphSelector6
            )
        }

        private val graphButtons by lazy {
            arrayOf(
                binding.overviewGraphBtn1,
                binding.overviewGraphBtn2,
                binding.overviewGraphBtn3,
                binding.overviewGraphBtn4,
                binding.overviewGraphBtn5,
                binding.overviewGraphBtn6,
                )
        }

        private fun getGraphHeight(percent: Long): Int {
            val maxHeight = 87
            val whiteHeight = (maxHeight * (percent * 0.01)).toInt()
            val density = App.instance.applicationContext.resources.displayMetrics.density
            return (whiteHeight * density).toInt()
        }

        private fun graphClickLogic(index: Int) {
            val graphScaleAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.scale_anim)
            val selectorOpenAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.tanslate_anim)
            val selectorCloseAnim = AnimationUtils.loadAnimation(App.instance.applicationContext, R.anim.reverse_translate_anim)
            graphScaleAnim.interpolator = LinearInterpolator()
            selectorOpenAnim.interpolator = LinearInterpolator()
            selectorCloseAnim.interpolator = LinearInterpolator()
            selectorCloseAnim.setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    selectors.forEach {
                        if (it.visibility == View.VISIBLE)
                            it.visibility = View.INVISIBLE
                    }
                    selectors[index].visibility = View.VISIBLE
                    selectors[index].startAnimation(selectorOpenAnim)
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
            selectorOpenAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    graphs.forEach { it.isClickable = true }
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
            graphs[index].startAnimation(graphScaleAnim)
            graphs.forEach {
                it.isClickable = false
            }
            selectors.forEach {
                if (it.visibility == View.VISIBLE)
                    it.startAnimation(selectorCloseAnim)
            }
        }

        fun bind(item: OverViewFragment.TimeInfo) {
            Log.i("adapter", "bind Called ${item.timeType}")
            binding.overviewGraphName.text =
                if (item.timeType == 0) "최근 주간 그래프" else "최근 월간 그래프"
            binding.overviewGraphType.text = if (item.timeType == 0) "(6주)" else "(6달)"
            for ((idx, btn) in graphButtons.withIndex()) {
                graphs[idx].layoutParams.height = getGraphHeight(item.accumulationTimes[idx])
                btn.setOnClickListener { graphClickLogic(idx) }
                graphs[idx].setOnClickListener { graphClickLogic(idx) }
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