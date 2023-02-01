package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.genesiseternity.incomemate.R
import com.google.android.material.card.MaterialCardView

class IconCategoryCardView(
    private val context: Context,
    gridLayout: androidx.gridlayout.widget.GridLayout,
    private var selectedColor: Int,
    private var selectedCardViewId: Int
) {
    private lateinit var imageCategoryType: TypedArray

    init {
        initChoiceCardView(gridLayout)
    }

    private lateinit var cardView: ArrayList<MaterialCardView>
    private lateinit var selectedCardView: View

    fun getSelectedCardViewId(): Int = selectedCardViewId

    fun setColorBackgroundCardView(selectedColor: Int)
    {
        this.selectedColor = selectedColor

        for (i in cardView.indices)
        {
            if (selectedCardView == cardView[i])
            {
                selectedCardViewId = i
                cardView[i].setCardBackgroundColor(selectedColor)
            }
        }
    }

    private fun initChoiceCardView(gridListCardView: androidx.gridlayout.widget.GridLayout)
    {
        if (selectedColor == 0)
            selectedColor = ContextCompat.getColor(context, R.color.green)

        val total: Int = 9
        val column: Int = 4
        val row: Int = total / column
        gridListCardView.columnCount = column
        gridListCardView.rowCount = row + 1

        fillGridLayoutCardView(gridListCardView)

        val countCardView: Int = gridListCardView.childCount
        cardView = ArrayList(countCardView)

        for (i in 0 until countCardView)
        {
            cardView.add(gridListCardView.getChildAt(i) as MaterialCardView)

            if (selectedCardViewId == i)
            {
                cardView[i].isChecked = true
                cardView[i].isClickable = false
                cardView[i].setCardBackgroundColor(selectedColor)
                cardView[i].toggle()

                selectedCardView = cardView[i]
            }

            cardView[i].setOnClickListener()
            {
                for (j in cardView.indices)
                {
                    if (it == cardView[j])
                    {
                        if (cardView[j].isClickable)
                        {
                            selectedCardView = it
                            selectedCardViewId = j

                            cardView[j].setCardBackgroundColor(selectedColor)
                            cardView[j].toggle()
                        }
                        cardView[j].isClickable = false
                    }
                    else // if (!cardView[i].isClickable())
                    {
                        cardView[j].setCardBackgroundColor(Color.WHITE)
                        cardView[j].isChecked = false
                        cardView[j].isClickable = true
                    }
                }
            }
        }
    }

    private fun fillGridLayoutCardView(gridLayout: androidx.gridlayout.widget.GridLayout)
    {
        imageCategoryType = context.resources.obtainTypedArray(R.array.image_category_type)

        val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)

        val margin: Int = convertDpToInt(8)
        //layoutParams.leftMargin = margin
        //layoutParams.rightMargin = margin
        //layoutParams.topMargin = margin
        //layoutParams.bottomMargin = margin

        layoutParams.leftMargin = margin
        layoutParams.rightMargin = margin
        layoutParams.topMargin = margin
        layoutParams.bottomMargin = margin

        //layoutParams.leftMargin = layoutParams.rightMargin = layoutParams.topMargin = layoutParams.bottomMargin = margin

        for (i in 0 until imageCategoryType.length())
        {
            val imageView: ImageView = ImageView(context)
            imageView.setImageResource(imageCategoryType.getResourceId(i, 0))
            imageView.layoutParams = ViewGroup.LayoutParams(convertDpToInt(70), convertDpToInt(70))

            val materialCardView: MaterialCardView = MaterialCardView(context)
            materialCardView.layoutParams = layoutParams
            materialCardView.radius = convertDpToInt(8).toFloat()
            materialCardView.cardElevation = convertDpToInt(6).toFloat()
            materialCardView.addView(imageView)

            gridLayout.addView(materialCardView)
        }
    }

    private fun convertDpToInt(value: Int): Int
    {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}