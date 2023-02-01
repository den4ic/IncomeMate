package com.genesiseternity.incomemate.colorPicker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.genesiseternity.incomemate.databinding.ActivityColorPickerBinding

class ColorPickerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityColorPickerBinding
    private lateinit var colorWheelPicker: ColorWheelPicker
    private lateinit var imgViewSelectedColorPicker: ImageView
    private lateinit var brightnessColorPicker: SeekBar
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        colorWheelPicker = binding.colorPicker
        imgViewSelectedColorPicker = binding.selectedColorPicker
        brightnessColorPicker = binding.brightnessColorPicker

        brightnessColorPicker.setOnSeekBarChangeListener(this)
        colorWheelPicker.setBrightness(brightnessColorPicker.progress)

        choiceColorPicker()
        selectedColor()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { colorWheelPicker.setBrightness(progress) }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    private fun choiceColorPicker()
    {
        colorWheelPicker.setDrawingCacheEnabled(true)
        colorWheelPicker.buildDrawingCache(true)

        colorWheelPicker?.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //if (!colorWheelPicker.pickerInsideCircle((int)motionEvent.getX(),(int)motionEvent.getY()))
                if (!colorWheelPicker.pickerInsideCircle(x = event?.getX()?.toDouble()!!, y = event?.getY()?.toDouble()!!))
                    return false

                //if (!colorWheelPicker.pickerInsideCircle(event.getX().toInt(), event.getY().toInt()))
                //    return false

                if (event?.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    bitmap = colorWheelPicker.getDrawingCache()
                    var pixel: Int = bitmap.getPixel(event.getX().toInt(), event.getY().toInt())

                    var r: Int = Color.red(pixel)
                    var g: Int = Color.green(pixel)
                    var b: Int = Color.blue(pixel)

                    //imgViewSelectedColorPicker.setBackgroundColor(Color.rgb(r, g, b))
                    imgViewSelectedColorPicker?.setBackgroundColor(Color.argb(brightnessColorPicker.getProgress(), r, g, b))
                }
                return true
            }
        })
    }

    private fun selectedColor()
    {
        //region get color in currency activity
        val getSelectedColorPicker: Int = intent.getIntExtra("selectedColorPicker", 0)
        imgViewSelectedColorPicker.setBackgroundColor(getSelectedColorPicker)
        //endregion

        binding.selectedNewColorBtn.setOnClickListener()
        {
            val temp: Int = (imgViewSelectedColorPicker.getBackground() as ColorDrawable).color
            //Log.i("324", "3 - " + temp)

            val intent = Intent()
            intent.putExtra("selectedColorPicker", temp)
            setResult(80, intent)
            finish()
        }

    }
}