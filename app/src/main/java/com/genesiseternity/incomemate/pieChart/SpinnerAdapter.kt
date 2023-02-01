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
import com.genesiseternity.incomemate.wallet.CurrencyAccountRecyclerModel

class SpinnerAdapter(context: Context, resource: Int, objects: MutableList<CurrencyAccountRecyclerModel>) :
    ArrayAdapter<CurrencyAccountRecyclerModel>(context, resource, objects)
{
    private val layoutInflater: LayoutInflater

    init
    {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        return fillView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        return fillView(position, convertView, parent)
    }

    private fun fillView(position: Int, convertView: View?, parent: ViewGroup) : View
    {
        val binding: RowSpinnerItemBinding = (convertView?.tag as? RowSpinnerItemBinding) ?: RowSpinnerItemBinding.inflate(layoutInflater, parent, false)

        val currencyAccountRecyclerModel: CurrencyAccountRecyclerModel? = getItem(position)
        val imgIconCurrencySpinner: ImageView = binding.imgIconCurrencySpinner
        val imageCurrencyType: TypedArray = binding.root.resources.obtainTypedArray(R.array.image_currency_type)

        binding.titleCurrencyNameSpinner.text = currencyAccountRecyclerModel!!.titleCurrencyName
        binding.amountCurrencySpinner.text = currencyAccountRecyclerModel.amountCurrency
        imgIconCurrencySpinner.setColorFilter(Color.parseColor("#2d3436"))
        imgIconCurrencySpinner.setImageDrawable(imageCurrencyType.getDrawable(currencyAccountRecyclerModel.imgIconCurrency))
        imgIconCurrencySpinner.setBackgroundColor(currencyAccountRecyclerModel.selectedColorId)

        return binding.root
    }
}