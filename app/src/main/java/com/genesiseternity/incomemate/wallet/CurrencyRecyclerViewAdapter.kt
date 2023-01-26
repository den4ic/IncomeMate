package com.genesiseternity.incomemate.wallet

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.RowItemBinding

class CurrencyRecyclerViewAdapter(private val iRecyclerView: IRecyclerView) :
    RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder>() {

    private var currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>

    init {
        currencyRecyclerModel = ArrayList()
    }

    fun getCurrencyRecyclerModel(): ArrayList<CurrencyRecyclerModel> = currencyRecyclerModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowItemBinding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, iRecyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rCurrency: CurrencyRecyclerModel = currencyRecyclerModel[position]

        holder.idCurrency.text = rCurrency.idCurrency.toString()
        holder.titleCurrencyName.text = rCurrency.titleCurrencyName

        if (rCurrency.amountCurrency.length == 1 && rCurrency.amountCurrency.startsWith("0"))
        {
            val amountCurrencyRes = rCurrency.amountCurrency + " " + holder.currencySymbol[rCurrency.currencyType]
            holder.amountCurrency.text = amountCurrencyRes
        }
        else
        {
            holder.amountCurrency.text = rCurrency.amountCurrency
        }

        if (rCurrency.imgIconCurrency != -1)
        {
            holder.imgIconCurrency.setImageDrawable(holder.imageCurrencyType.getDrawable(rCurrency.imgIconCurrency))
        }
        else
        {
            holder.imgIconCurrency.setImageDrawable(holder.drawAddingBtn)
        }

        holder.imgIconCurrency.setBackgroundColor(rCurrency.selectedColorId)

        /*
        String haveNegative = holder.amountCurrency.getText().toString()
        if (haveNegative.startsWith("-"))
        {
            holder.amountCurrency.setTextColor(Color.parseColor("#d63031"))
        }
        else if (!haveNegative.startsWith("-"))
        {
            holder.amountCurrency.setTextColor(Color.parseColor("#00b894"))
        }
        else if (haveNegative.startsWith("0"))
        {
            holder.amountCurrency.setTextColor(Color.parseColor("#2d3436"))
        }
        */

    }

    override fun getItemCount(): Int {
        return currencyRecyclerModel.size
    }

    fun setDataCurrencyRecyclerModel(currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>)
    {
        val diffCallBack = CurrencyRecyclerModelDiffUtil(this.currencyRecyclerModel, currencyRecyclerModel)
        val diffRes = DiffUtil.calculateDiff(diffCallBack)
        this.currencyRecyclerModel = currencyRecyclerModel
        //notifyDataSetChanged()
        diffRes.dispatchUpdatesTo(this)
    }

    fun addCurrencyRecyclerModelToPos(pos: Int, newCurrencyRecyclerModel: CurrencyRecyclerModel)
    {
        this.currencyRecyclerModel.add(pos, newCurrencyRecyclerModel)
        notifyItemInserted(pos)
    }

    fun deleteCurrencyRecyclerModel(currencyRecyclerModel: CurrencyRecyclerModel)
    {
        val indexCurrency: Int = findIndexById(currencyRecyclerModel.idCurrency)
        if (indexCurrency != -1)
        {
            this.currencyRecyclerModel = ArrayList(this.currencyRecyclerModel)
            this.currencyRecyclerModel.removeAt(indexCurrency)
            notifyItemRemoved(indexCurrency)
        }
    }

    private fun findIndexById(idCurrency: Int): Int = currencyRecyclerModel.indexOfFirst { it.idCurrency == idCurrency }

    //class ViewHolder(itemView: View, iRecyclerView: IRecyclerView) : RecyclerView.ViewHolder(itemView)
    class ViewHolder(binding: RowItemBinding, iRecyclerView: IRecyclerView) : RecyclerView.ViewHolder(binding.root)
    {
        val idCurrency: TextView
        val titleCurrencyName: TextView
        val amountCurrency: TextView
        val imgIconCurrency: ImageView
        val currencySymbol: Array<String>
        val imageCurrencyType: TypedArray
        val drawAddingBtn: Drawable

        private val bindingRoot = binding.root

        init
        {
            //idCurrency = itemView.findViewById(R.id.idCurrency)
            //titleCurrencyName = itemView.findViewById(R.id.titleCurrencyName)
            //amountCurrency = itemView.findViewById(R.id.amountCurrency)
            //imgIconCurrency = itemView.findViewById(R.id.imgIconCurrency)
            //imgIconCurrency.setColorFilter(Color.parseColor("#2d3436"))
            //currencySymbol = itemView.resources.getStringArray(R.array.list_currency_symbol)

            idCurrency = binding.idCurrency
            titleCurrencyName = binding.titleCurrencyName
            amountCurrency = binding.amountCurrency
            imgIconCurrency = binding.imgIconCurrency
            imgIconCurrency.setColorFilter(Color.parseColor("#2d3436"))

            currencySymbol = bindingRoot.resources.getStringArray(R.array.list_currency_symbol)
            drawAddingBtn = ResourcesCompat.getDrawable(bindingRoot.resources, R.drawable.ic_baseline_add_circle_24, null)!!
            imageCurrencyType = bindingRoot.resources.obtainTypedArray(R.array.image_currency_type)

            //itemView.setOnClickListener()
            bindingRoot.setOnClickListener()
            {
                val pos: Int = adapterPosition

                if (pos != RecyclerView.NO_POSITION) {
                    iRecyclerView.onItemClick(pos)
                }
            }
        }
    }

    class CurrencyRecyclerModelDiffUtil(
        private val oldList: ArrayList<CurrencyRecyclerModel>,
        private val newList: ArrayList<CurrencyRecyclerModel>) : DiffUtil.Callback()
    {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCurrencyRecyclerModel = oldList[oldItemPosition]
            val newCurrencyRecyclerModel = newList[newItemPosition]
            return oldCurrencyRecyclerModel.idCurrency == newCurrencyRecyclerModel.idCurrency
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldCurrencyRecyclerModel = oldList[oldItemPosition]
            val newCurrencyRecyclerModel = newList[newItemPosition]
            return oldCurrencyRecyclerModel == newCurrencyRecyclerModel
        }
    }
}