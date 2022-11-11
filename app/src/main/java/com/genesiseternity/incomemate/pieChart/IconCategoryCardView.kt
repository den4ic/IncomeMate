package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.genesiseternity.incomemate.R
import com.google.android.material.card.MaterialCardView

class IconCategoryCardView(
    private var context: Context,
    gridLayout: androidx.gridlayout.widget.GridLayout,
    private var selectedColor: Int,
    private var selectedCardViewId: Int
) {

    private lateinit var imageCategoryType: TypedArray
    //private MaterialCardView[] materialCardViewList

    /*
    public IconCategoryCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs)
    }

    public IconCategoryCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr)
    }
    */

    init {
        initChoiceCardView(gridLayout)
        //init()
    }


    //private androidx.gridlayout.widget.GridLayout gridListCardView
    private lateinit var cardView: ArrayList<MaterialCardView>
    private lateinit var selectedCardView: View

    public fun getSelectedCardViewId(): Int
    {
        return selectedCardViewId
    }

    fun setColorBackgroundCardView(selectedColor: Int)
    {
        this.selectedColor = selectedColor

        for (i in cardView.indices)
        {
            if (selectedCardView == cardView[i])
            {
                selectedCardViewId = i
                //Log.d("123", "1111 --- " + selectedColor)
                cardView[i].setCardBackgroundColor(selectedColor)
            }
        }
    }

    private fun initChoiceCardView(gridListCardView: androidx.gridlayout.widget.GridLayout)
    {
        if (selectedColor == 0)
            selectedColor = context.getResources().getColor(R.color.green)

        //gridListCardView = binding.gridViewCategoryIcon

        val total: Int = 9
        val column: Int = 4
        val row: Int = total / column
        gridListCardView.setColumnCount(column)
        gridListCardView.setRowCount(row + 1)

        fillGridLayoutCardView(gridListCardView)

        val countCardView: Int = gridListCardView.childCount
        cardView = ArrayList(countCardView)

        for (i in 0 until countCardView)
        {
            cardView.add(gridListCardView.getChildAt(i) as MaterialCardView)

            if (selectedCardViewId == i)
            {
                cardView[i].setCardBackgroundColor(selectedColor)
                cardView[i].isChecked = true
                cardView[i].isClickable = false
                cardView[i].toggle()

                selectedCardView = cardView[i]
            }

            cardView[i].setOnClickListener()
            {
                for (j in cardView.indices)
                {
                    if (it == cardView[j])
                    {
                        if (cardView[j].isClickable())
                        {
                            selectedCardView = it
                            selectedCardViewId = j

                            cardView[j].setCardBackgroundColor(selectedColor)
                            cardView[j].toggle()
                        }
                        cardView[j].setClickable(false)
                    }
                    else // if (!cardView[i].isClickable())
                    {
                        cardView[j].setCardBackgroundColor(Color.WHITE)
                        cardView[j].setChecked(false)
                        cardView[j].setClickable(true)
                    }
                }
            }
        }
    }

    private fun fillGridLayoutCardView(gridLayout: androidx.gridlayout.widget.GridLayout)
    {
        // R.array.image_category_type | R.array.image_currency_type
        //imageCategoryType = getResources().obtainTypedArray(R.array.image_currency_type)
        imageCategoryType = context.getResources().obtainTypedArray(R.array.image_category_type)
        //materialCardViewList = new MaterialCardView[imageCategoryType.length()]

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

            //materialCardViewList[i] = materialCardView
            gridLayout.addView(materialCardView)
        }
    }

    private fun convertDpToInt(value: Int): Int
    {
        return (value * context.resources.displayMetrics.density).toInt()
    }

    //private int convertDpToInt(int value)
    //{
    //    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics())
    //}

    //private void fillGridLayoutCardView(GridLayout gridLayout)
    //{
    //    for (int i = 0 i < materialCardViewList.length i++)
    //    {
    //        gridLayout.addView(materialCardViewList[i])
    //    }
    //}

}