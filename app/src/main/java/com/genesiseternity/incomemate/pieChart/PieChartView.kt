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

class PieChartView : View {

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

    //private Paint huePaint = new Paint(Paint.ANTI_ALIAS_FLAG)
    //private RectF rectF = new RectF(0f, 0f, 1000f, 1000f)
    //private float centerX
    //private float centerY
    //private float radius

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

    //public PieChartView(Context context, int[] data, int[] color) {
    //    super(context)
    //    setFocusable(true)
    //    this.data = data
    //    this.color = color
    //}

    private var lazyInitDraw: Boolean = false

    fun setDataPieChart(data: FloatArray, color: IntArray)
    {
        isFocusable = true
        this.data = data
        this.color = color

        //paintArc = new Paint()
        //dividerPaint = new Paint()
        //paintInsideCircle = new Paint()
        paintArc.isAntiAlias = true

        if (hasData(data)) {
            lazyInitDraw = true
            initAnimRotate()
        }
    }

    fun hasData(data: FloatArray) : Boolean
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

    fun initAnimRotate()
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
            //Log.d("PieChartView", i + " tempAngle - " + tempAngle[i])
        }
        //startAngle = startAngle + scaledValues[i]

        val animation: CircleAngleAnimation = CircleAngleAnimation(this, tempAngle)
        animation.duration = 1000
        this.startAnimation(animation)
    }

    private val CIRCLE_THICKNESS: Int = 30

    //private float startAngle = 0
    //private int[] data = {}
    //private int[] color = {}
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var data: FloatArray = floatArrayOf(1.0f)
    private var color: IntArray = intArrayOf(Color.parseColor("#b2bec3"))
    private val defaultColor: Int = Color.parseColor("#b2bec3")

    //private int width
    //int numberOfparts//it tells many data or item will be placed in chart
    //int[] data={6,5,8,4,7,6}
    //int[] color={Color.RED,Color.YELLOW,Color.CYAN,Color.GREEN,Color.MAGENTA, Color.BLUE}
    //int[] data={5,5,5}
    //int[] color={Color.BLUE,Color.GREEN,Color.RED}

    //int x = 0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas.drawCircle(centerX, centerY, radius, huePaint)


        //canvas.drawColor(Color.RED)
        //centerX = (float)(getWidth() / 2 )
        //centerY = (float)(getHeight() / 2 )
        //canvas.drawCircle(centerX,centerY, 200.0f, huePaint)
        //canvas.drawArc(rectF, 0, 90, true, huePaint)

        //Log.d("PieChartView", " - " + data.length)

        //if (data.length <= 0)
        //{
        //    Log.d("PieChartView", " - " + data.length)
        //    return
        //}

        canvas?.drawColor(Color.WHITE)
        //Paint paintArc = new Paint()
        //paintArc.setAntiAlias(true)
        //paintArc.setColor(color[0])
        //paintArc.setStyle(Paint.Style.STROKE)
        //paintArc.setStrokeWidth(0)
        //paintArc.setStyle(Paint.Style.FILL)

        //float[] scaledValues = scale()
        val rectF: RectF = RectF(0f, 0f, width.toFloat(), width.toFloat())
        //paintArc.setColor(Color.BLACK)


        // divider chart
        //Paint dividerPaint = new Paint()
        dividerPaint.color = Color.WHITE
        dividerPaint.style = Paint.Style.STROKE
        //dividerPaint.setTextSize(50)
        dividerPaint.strokeWidth = 14f

            //if (angle == null)
            //    canvas.drawArc(rectF, 90, 360, true, paintArc)


        if (lazyInitDraw) {
            //Log.d("PieChartView", " - " + angle.length)

            for (i in angles.indices) {
                paintArc.color = color[i]
                paintArc.style = Paint.Style.FILL
                //canvas.drawArc(rectF, startAngle, scaledValues[i],true, dividerPaint)
                //canvas.drawArc(rectF, startAngle, scaledValues[i],true, paintArc)
                //startAngle = startAngle + scaledValues[i]

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

        //Paint paintInsideCircleBorder = new Paint()
        //paintInsideCircleBorder.setStyle(Paint.Style.STROKE)
        //paintInsideCircleBorder.setColor(Color.argb(20, 0,0,0))
        //paintInsideCircleBorder.setStrokeWidth(50)
        //canvas.drawCircle(centerX, centerY, radius, paintInsideCircleBorder)

        //Paint paintInsideCircle = new Paint()
        paintInsideCircle.setStyle(Paint.Style.FILL)
        paintInsideCircle.setColor(Color.WHITE)
        canvas?.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            radius.toFloat(),
            paintInsideCircle
        )


        //x++
        //invalidate() // redraw
    }



    private fun scale(): FloatArray
    {
        val scaledValues: FloatArray = FloatArray(this.data.size)
        val total: Float = getTotal() //Total all values supplied to the chart

        for (i in this.data.indices)
        {
            scaledValues[i] = (this.data[i] / total) * 360 //Scale each value
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