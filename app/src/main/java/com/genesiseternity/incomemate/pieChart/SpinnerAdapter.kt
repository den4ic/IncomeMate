package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerModel

class SpinnerAdapter(context: Context, resource: Int, objects: MutableList<CurrencyRecyclerModel>) :
    ArrayAdapter<CurrencyRecyclerModel>(context, resource, objects) {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = layoutInflater.inflate(R.layout.row_spinner_item, null, true)
        val currencyRecyclerModel: CurrencyRecyclerModel? = getItem(position)

        val titleCurrencyNameSpinner: TextView = rowView.findViewById(R.id.titleCurrencyNameSpinner)
        val amountCurrencySpinner: TextView = rowView.findViewById(R.id.amountCurrencySpinner)
        val imgIconCurrencySpinner: ImageView = rowView.findViewById(R.id.imgIconCurrencySpinner)

        titleCurrencyNameSpinner.text = currencyRecyclerModel!!.titleCurrencyName
        amountCurrencySpinner.text = currencyRecyclerModel.amountCurrency
        imgIconCurrencySpinner.setImageResource(currencyRecyclerModel.imgIconCurrency)
        imgIconCurrencySpinner.setBackgroundColor(currencyRecyclerModel.selectedColorId)
        return rowView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val res = convertView ?: layoutInflater.inflate(R.layout.row_spinner_item, null, true)

        val currencyRecyclerModel: CurrencyRecyclerModel? = getItem(position)
        val titleCurrencyNameSpinner: TextView = res!!.findViewById(R.id.titleCurrencyNameSpinner)
        val amountCurrencySpinner: TextView = res.findViewById(R.id.amountCurrencySpinner)
        val imgIconCurrencySpinner: ImageView = res.findViewById(R.id.imgIconCurrencySpinner)

        titleCurrencyNameSpinner.text = currencyRecyclerModel!!.titleCurrencyName
        amountCurrencySpinner.text = currencyRecyclerModel.amountCurrency
        imgIconCurrencySpinner.setImageResource(currencyRecyclerModel.imgIconCurrency)
        imgIconCurrencySpinner.setBackgroundColor(currencyRecyclerModel.selectedColorId)
        return res
    }
}