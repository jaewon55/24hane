package com.hane24.hoursarenotenough24.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Integer.min

class CustomProgressbar(context: Context, attrs: AttributeSet?): View(context, attrs) {
    constructor(context: Context): this(context, null)

    var maxProgress: Float = 0.0f
        set(value) {
            field = value
        }

    var currProgress: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint()

    private fun foregroundGradient(): SweepGradient {
        val size = (min(super.getHeight(), super.getWidth()) - 10).toFloat()
        val colors = intArrayOf(Color.parseColor("#8EF2F3"), Color.parseColor("#735BF2"))
        val positions = floatArrayOf(0f, 1f)

        return SweepGradient(size - 10 / 2, size - 10 / 2,  colors, positions)
    }

    private fun backgroundGradient(): SweepGradient {
        val size = (min(super.getHeight(), super.getWidth()) - 10).toFloat()
        val colors = intArrayOf(Color.parseColor("#33BCF8F9"), Color.parseColor("#33735BF2"))
        val positions = floatArrayOf(0f, 1f)

        return SweepGradient(size - 10 / 2, size - 10 / 2,  colors, positions)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val size = (min(super.getHeight(), super.getWidth()) - 10).toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        paint.shader = backgroundGradient()
        canvas?.drawArc(10f, 10f, size, size, 0f, 360f, false, paint)

        paint.shader = foregroundGradient()
        canvas?.drawArc(10f, 10f, size, size, 0f, currProgress * 3.6f, false, paint)
    }
}