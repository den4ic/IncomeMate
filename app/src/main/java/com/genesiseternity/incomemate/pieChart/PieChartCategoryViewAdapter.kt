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
    pieChartCategoryArrayList: ArrayList<PieChartCategory>,
    private val iPieChartCategoryView: IPieChartCategoryView
) : ArrayAdapter<PieChartCategory>(context, 0, pieChartCategoryArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val res = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_category_item, parent, false)

        val pieChartCategory: PieChartCategory? = getItem(position)

        val titleCategory: TextView = res.findViewById(R.id.titleCategory)
        val imageCategory: ImageView = res.findViewById(R.id.imageCategory)
        val amountCategory: TextView = res.findViewById(R.id.amountCategory)
        val currencySymbol: Array<String> = res.resources.getStringArray(R.array.list_currency_symbol)

        titleCategory.text = pieChartCategory!!.titleCategoryName
        imageCategory.setImageResource(pieChartCategory.imageCategory)
        //amountCategory.setText(pieChartCategory.getAmountCategory())
        imageCategory.setBackgroundColor(pieChartCategory.selectedColorId)


        if (pieChartCategory.idCategory != -1)
        {
            //amountCategory.setText(pieChartCategory.getAmountCategory() + " " + currencySymbol[pieChartCategory.getCurrencyType()])
            if (pieChartCategory.amountCategory.length == 1 && pieChartCategory.amountCategory.startsWith("0"))
            {
                amountCategory.text = pieChartCategory.amountCategory + " " + currencySymbol[pieChartCategory.currencyType]
            }
            else
            {
                amountCategory.text = pieChartCategory.amountCategory
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