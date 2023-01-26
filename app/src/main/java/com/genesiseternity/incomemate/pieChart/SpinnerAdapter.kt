package com.genesiseternity.incomemate.pieChart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.RowSpinnerItemBinding
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerModel

class SpinnerAdapter(context: Context, resource: Int, objects: MutableList<CurrencyRecyclerModel>) :
    ArrayAdapter<CurrencyRecyclerModel>(context, resource, objects) {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return fillView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return fillView(position, convertView, parent)
    }

    private fun fillView(position: Int, convertView: View?, parent: ViewGroup) : View {
        val binding: RowSpinnerItemBinding = (convertView?.tag as? RowSpinnerItemBinding) ?: RowSpinnerItemBinding.inflate(layoutInflater, parent, false)
        //binding.root.tag = binding

        val currencyRecyclerModel: CurrencyRecyclerModel? = getItem(position)
        val imgIconCurrencySpinner: ImageView = binding.imgIconCurrencySpinner
        val imageCurrencyType: TypedArray = binding.root.resources.obtainTypedArray(R.array.image_currency_type)

        binding.titleCurrencyNameSpinner.text = currencyRecyclerModel!!.titleCurrencyName
        binding.amountCurrencySpinner.text = currencyRecyclerModel.amountCurrency
        imgIconCurrencySpinner.setColorFilter(Color.parseColor("#2d3436"))
        imgIconCurrencySpinner.setImageDrawable(imageCurrencyType.getDrawable(currencyRecyclerModel.imgIconCurrency))
        //imgIconCurrencySpinner.setImageResource(imageCurrencyType.getResourceId(currencyRecyclerModel.imgIconCurrency, 0))
        imgIconCurrencySpinner.setBackgroundColor(currencyRecyclerModel.selectedColorId)

        return binding.root
    }
}