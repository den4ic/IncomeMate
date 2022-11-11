package com.genesiseternity.incomemate.colorPicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class ColorWheelPicker : View {

    private val DEFAULT_BRIGHTNESS: Int = 230

    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var radius: Float = 0.0f

    private var huePaint: Paint = Paint()
    private var saturationPaint: Paint = Paint()
    private var brightnessOverlayPaint: Paint = Paint()

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
    {
        huePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        saturationPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        brightnessOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        brightnessOverlayPaint.color = Color.BLACK
        brightnessOverlayPaint.alpha = brightnessToAlpha(DEFAULT_BRIGHTNESS)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = Math.min(w, h) * 0.48f
        centerX = w * 0.5f
        centerY = h * 0.5f
        recomputeShader()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(centerX, centerY, radius, huePaint)
        canvas?.drawCircle(centerX, centerY, radius, saturationPaint)
        canvas?.drawCircle(centerX, centerY, radius, brightnessOverlayPaint)
    }

    fun setBrightness(brightness: Int)
    {
        brightnessOverlayPaint.alpha = brightnessToAlpha(brightness)
        invalidate()
    }

    private fun recomputeShader()
    {
        val tempIntArray: IntArray = intArrayOf(
            Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN,
            Color.GREEN, Color.YELLOW, Color.RED)

        val tempFloatArray: FloatArray = floatArrayOf(
            0.000f, 0.166f, 0.333f, 0.499f,
            0.666f, 0.833f, 0.999f)

        val hueShader: Shader = SweepGradient(centerX, centerY, tempIntArray, tempFloatArray)

        huePaint.shader = hueShader

        val satShader: Shader = RadialGradient(centerX, centerY, radius,
            Color.WHITE, 0x00FFFFFF,
            Shader.TileMode.CLAMP)

        saturationPaint.shader = satShader
    }

    fun pickerInsideCircle(x: Double, y: Double): Boolean
    {
        return Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) < radius * radius
    }

    private fun brightnessToAlpha(brightness: Int): Int
    {
        return 255 - brightness
    }


}