package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.content.res.TypedArray
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.RowCategoryItemBinding

class PieChartCategoryViewAdapter(
    context: Context,
    pieChartCategoryModelArrayList: ArrayList<PieChartCategoryModel>,
    private val iPieChartCategoryView: IPieChartCategoryView
) : ArrayAdapter<PieChartCategoryModel>(context, 0, pieChartCategoryModelArrayList)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: RowCategoryItemBinding = (convertView?.tag as? RowCategoryItemBinding) ?: RowCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val pieChartCategoryModel: PieChartCategoryModel? = getItem(position)

        val titleCategory: TextView = binding.titleCategory
        val imageCategory: ImageView = binding.imageCategory
        val amountCategory: TextView = binding.amountCategory
        val bindingRoot = binding.root

        titleCategory.text = pieChartCategoryModel!!.titleCategoryName
        imageCategory.setBackgroundColor(pieChartCategoryModel.selectedColorId)

        if (pieChartCategoryModel.idCategory != -1)
        {
            val imageCategoryType: TypedArray = context.resources.obtainTypedArray(R.array.image_category_type)
            imageCategory.setImageResource(imageCategoryType.getResourceId(pieChartCategoryModel.imageCategory, 0))

            if (pieChartCategoryModel.amountCategory.length == 1 && pieChartCategoryModel.amountCategory.startsWith("0"))
            {
                val currencySymbol: Array<String> = context.resources.getStringArray(R.array.list_currency_symbol)
                val amountCategoryRes = pieChartCategoryModel.amountCategory + " " + currencySymbol[pieChartCategoryModel.currencyType]
                amountCategory.text = amountCategoryRes
            }
            else
            {
                amountCategory.text = pieChartCategoryModel.amountCategory
            }
        }
        else
        {
            imageCategory.setImageResource(R.drawable.ic_baseline_add_circle_24)
            amountCategory.text = ""
        }

        bindingRoot.setOnClickListener { iPieChartCategoryView.onItemClick(position) }
        return bindingRoot
    }
}