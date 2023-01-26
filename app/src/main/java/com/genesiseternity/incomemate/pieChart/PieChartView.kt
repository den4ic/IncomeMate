package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class PieChartView : View
{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val paintArc: Paint = Paint()
    private val dividerPaint: Paint = Paint()
    private val paintInsideCircle: Paint = Paint()

    private lateinit var angles: FloatArray
    fun getAngles(): FloatArray {
        return angles
    }
    fun setAngles(angles: FloatArray) {
        this.angles = angles
    }

    private var lazyInitDraw: Boolean = false

    fun setDataPieChart(data: FloatArray, color: IntArray)
    {
        isFocusable = true
        this.data = data
        this.color = color

        paintArc.isAntiAlias = true

        if (hasData(data)) {
            lazyInitDraw = true
            initAnimRotate()
        }
    }

    private fun hasData(data: FloatArray) : Boolean
    {
        for (i in 0 until data.size-1)
        {
            if (data[i] != data[i + 1])
            {
                return true
            }
        }
        return false
    }

    private fun initAnimRotate()
    {
        angles = FloatArray(scale().size)
        val tempAngle: FloatArray = FloatArray(angles.size)

        for (i in 0 until scale().size)
        {
            tempAngle[i] = scale()[i]
        }

        for (i in angles.size - 1 downTo 0)
        {
            if (i < angles.size - 1)
            {
                tempAngle[i] = tempAngle[i + 1] + tempAngle[i]
            }
        }

        val animation: CircleAngleAnimation = CircleAngleAnimation(this, tempAngle)
        animation.duration = 1000
        this.startAnimation(animation)
    }

    private val CIRCLE_THICKNESS: Int = 30
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var data: FloatArray = floatArrayOf(1.0f)
    private var color: IntArray = intArrayOf(Color.parseColor("#b2bec3"))
    private val defaultColor: Int = Color.parseColor("#b2bec3")

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.WHITE)
        val rectF: RectF = RectF(0f, 0f, width.toFloat(), width.toFloat())

        dividerPaint.color = Color.WHITE
        dividerPaint.style = Paint.Style.STROKE
        //dividerPaint.setTextSize(50)
        dividerPaint.strokeWidth = 14f

        if (lazyInitDraw) {
            for (i in angles.indices) {
                paintArc.color = color[i]
                paintArc.style = Paint.Style.FILL
                canvas?.drawArc(rectF, 90f, angles[i], true, dividerPaint)
                canvas?.drawArc(rectF, 90f, angles[i], true, paintArc)
            }
        }
        else
        {
            paintArc.color = defaultColor
            canvas?.drawArc(rectF, 0f, 360f, true, paintArc)
        }

        val radius: Int = (width / 2) - CIRCLE_THICKNESS
        centerX = width / 2
        centerY = centerX

        paintInsideCircle.style = Paint.Style.FILL
        paintInsideCircle.color = Color.WHITE
        canvas?.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            radius.toFloat(),
            paintInsideCircle
        )
    }

    private fun scale(): FloatArray
    {
        val scaledValues: FloatArray = FloatArray(this.data.size)
        val total: Float = getTotal() // Total all values supplied to the chart

        for (i in this.data.indices)
        {
            scaledValues[i] = (this.data[i] / total) * 360 // Scale each value
        }

        return scaledValues
    }

    private fun getTotal() : Float
    {
        var total: Float = 0.0f

        for (i in this.data)
        {
            total += i
        }
        return total
    }

    class CircleAngleAnimation(private var circle: PieChartView, private var newAngle: FloatArray) : Animation()
    {
        private var oldAngle: FloatArray = circle.getAngles()
        private var angle: FloatArray = FloatArray(newAngle.size)

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)

            for (i in angle.indices)
            {
                angle[i] = oldAngle[i] + ((newAngle[i] - oldAngle[i]) * interpolatedTime)
            }
            circle.setAngles(angle)
            circle.requestLayout()
        }
    }
}