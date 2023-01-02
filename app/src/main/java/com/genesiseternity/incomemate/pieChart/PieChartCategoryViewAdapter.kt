package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.genesiseternity.incomemate.R

class PieChartCategoryViewAdapter(
    context: Context,
    pieChartCategoryModelArrayList: ArrayList<PieChartCategoryModel>,
    private val iPieChartCategoryView: IPieChartCategoryView
) : ArrayAdapter<PieChartCategoryModel>(context, 0, pieChartCategoryModelArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val res = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_category_item, parent, false)

        val pieChartCategoryModel: PieChartCategoryModel? = getItem(position)

        val titleCategory: TextView = res.findViewById(R.id.titleCategory)
        val imageCategory: ImageView = res.findViewById(R.id.imageCategory)
        val amountCategory: TextView = res.findViewById(R.id.amountCategory)
        val currencySymbol: Array<String> = res.resources.getStringArray(R.array.list_currency_symbol)

        titleCategory.text = pieChartCategoryModel!!.titleCategoryName
        imageCategory.setImageResource(pieChartCategoryModel.imageCategory)
        //amountCategory.setText(pieChartCategory.getAmountCategory())
        imageCategory.setBackgroundColor(pieChartCategoryModel.selectedColorId)


        if (pieChartCategoryModel.idCategory != -1)
        {
            //amountCategory.setText(pieChartCategory.getAmountCategory() + " " + currencySymbol[pieChartCategory.getCurrencyType()])
            if (pieChartCategoryModel.amountCategory.length == 1 && pieChartCategoryModel.amountCategory.startsWith("0"))
            {
                amountCategory.text = pieChartCategoryModel.amountCategory + " " + currencySymbol[pieChartCategoryModel.currencyType]
            }
            else
            {
                amountCategory.text = pieChartCategoryModel.amountCategory
            }
        }
        else
        {
            amountCategory.text = ""
        }

        res.setOnClickListener { iPieChartCategoryView.onItemClick(position) }
        return res
    }

}