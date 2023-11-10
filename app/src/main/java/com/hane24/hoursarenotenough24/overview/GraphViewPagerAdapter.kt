package com.hane24.hoursarenotenough24.overview

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.hane24.hoursarenotenough24.App
import com.hane24.hoursarenotenough24.R
import com.hane24.hoursarenotenough24.databinding.FragmentOverviewGraphViewBinding

class GraphViewPagerAdapter(
    private var items: List<GraphInfo>
): RecyclerView.Adapter<GraphViewPagerAdapter.GraphViewHolder>() {

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

    fun setItem(item: List<GraphInfo>) {
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

        private val graphBtn by lazy {
            arrayOf(
                binding.overviewGraphBtn1,
                binding.overviewGraphBtn2,
                binding.overviewGraphBtn3,
                binding.overviewGraphBtn4,
                binding.overviewGraphBtn5,
                binding.overviewGraphBtn6,
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
                    binding.selectIndex = index
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

        fun bind(item: GraphInfo) {
            binding.graphInfo = item
            binding.selectIndex = 0
            binding.density = itemView.context.resources.displayMetrics.density
            binding.isMonth = adapterPosition == 1

            for ((idx, btn) in graphs.withIndex()) {
                btn.setOnClickListener { graphClickLogic(idx) }
                graphs[idx].setOnClickListener { graphClickLogic(idx) }
                graphBtn[idx].setOnClickListener { graphClickLogic(idx) }
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