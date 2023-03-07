package com.hane24.hoursarenotenough24.overview

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewGraphViewBinding
import com.hane24.hoursarenotenough24.utils.TodayCalendarUtils
import java.text.SimpleDateFormat
import java.util.*

class GraphViewPagerAdapter(): RecyclerView.Adapter<GraphViewPagerAdapter.GraphViewHolder>() {
    private val items: MutableList<OverViewFragment.TimeInfo> =
        mutableListOf(
            OverViewFragment.TimeInfo(listOf(10L,10L, 10L, 10L, 10L, 10L), 0),
            OverViewFragment.TimeInfo(listOf(10L, 10L, 10L, 10L, 10L, 10L), 1)
        )
    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphViewHolder {
        return GraphViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GraphViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItem(item: List<OverViewFragment.TimeInfo>) {
        items.clear()
        item.forEach { items += it }
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
            val whiteHeight = (maxHeight * (percent * 0.01)).toInt() + 10
            val density = App.instance.applicationContext.resources.displayMetrics.density
            return (whiteHeight * density).toInt()
        }

        private fun parseTimeToPercent(items: List<Long>): List<Long> {
            val maxItem = items.maxOf { it }
            return items.map{ (it.toDouble() / maxItem * 100L).toLong() }
        }

        private fun setTotalTime(index: Int, item: OverViewFragment.TimeInfo) {
            val totalTime = item.accumulationTimes[index] / 3600.0

            binding.overviewGraphTotalTime.text =
                App.instance.applicationContext.resources.getString(R.string.overview_graph_total_time, totalTime)
        }

        private fun setAverageTime(index: Int, item: OverViewFragment.TimeInfo) {
            val days = if (item.timeType == 0) 7 else 30
            val averageTime = item.accumulationTimes[index] / 3600.0 / days

            binding.overviewGraphAverageTime.text =
                App.instance.applicationContext.resources.getString(R.string.overview_graph_average_time, averageTime)
        }

        private fun setDateText(index: Int, item: OverViewFragment.TimeInfo) {
            if (item.timeType == 1) {
                val format = SimpleDateFormat("yyyy.M", Locale("ko"))
                val calendar = Calendar.getInstance()

                calendar.set(TodayCalendarUtils.year, TodayCalendarUtils.month-1-index, 1)
                val toDate = format.format(calendar.time)

                binding.overviewGraphDateInfo.text = toDate
            } else {
                val format = SimpleDateFormat("M.d(E)", Locale("ko"))
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -(index*7))

                val (toDate, fromDate) = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> {
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.TUESDAY -> {
                        calendar.add(Calendar.DATE, -1)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.WEDNESDAY -> {
                        calendar.add(Calendar.DATE, -2)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.THURSDAY -> {
                        calendar.add(Calendar.DATE, -3)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.FRIDAY -> {
                        calendar.add(Calendar.DATE, -4)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.SATURDAY -> {
                        calendar.add(Calendar.DATE, -5)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    Calendar.SUNDAY -> {
                        calendar.add(Calendar.DATE, -6)
                        val toDate = format.format(calendar.time)
                        calendar.add(Calendar.DATE, 6)
                        val fromDate = format.format(calendar.time)
                        toDate to fromDate
                    }
                    else -> throw IllegalStateException("Exception")
                }
                binding.overviewGraphDateInfo.text =
                    App.instance.resources.getString(R.string.overview_graph_date, toDate, fromDate)
            }
        }

        private fun graphClickLogic(index: Int, item: OverViewFragment.TimeInfo) {
            setTotalTime(index, item)
            setAverageTime(index, item)
            setDateText(index, item)
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
            binding.overviewGraphName.text =
                if (item.timeType == 0) "최근 주간 그래프" else "최근 월간 그래프"
            binding.overviewGraphType.text = if (item.timeType == 0) "(6주)" else "(6개월)"
            setTotalTime(0, item)
            setAverageTime(0, item)
            setDateText(0, item)
            selectors.forEachIndexed { idx, it ->
                if (idx == 0)
                    it.visibility = View.VISIBLE
                else
                    it.visibility = View.INVISIBLE
            }

            val percents = parseTimeToPercent(item.accumulationTimes)

            for ((idx, btn) in graphButtons.withIndex()) {
                graphs[idx].layoutParams.height = getGraphHeight(percents[idx])
                btn.setOnClickListener { graphClickLogic(idx, item) }
                graphs[idx].setOnClickListener { graphClickLogic(idx, item) }
                graphs[idx].requestLayout()
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