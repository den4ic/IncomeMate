package com.genesiseternity.incomemate.wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.R

class CurrencyRecyclerViewAdapter(context: Context, private val iRecyclerView: IRecyclerView) :
    RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private var currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>

    //constructor(Context context, List<CurrencyRecyclerModel> currencyRecyclerModel, IRecyclerView iRecyclerView)
    init {
        //this.currencyRecyclerModel = currencyRecyclerModel
        currencyRecyclerModel = ArrayList()
        this.inflater = LayoutInflater.from(context)
    }

    fun getCurrencyRecyclerModel(): ArrayList<CurrencyRecyclerModel>
    {
        return currencyRecyclerModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.row_item, parent, false)
        return ViewHolder(view, iRecyclerView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val rCurrency: CurrencyRecyclerModel = currencyRecyclerModel[position]

        holder.idCurrency.text = rCurrency.idCurrency.toString()
        holder.titleCurrencyName.text = rCurrency.titleCurrencyName

        if (rCurrency.amountCurrency.length == 1 && rCurrency.amountCurrency.startsWith("0"))
        {
            holder.amountCurrency.text = rCurrency.amountCurrency + " " + holder.currencySymbol[rCurrency.currencyType]
        }
        else
        {
            holder.amountCurrency.text = rCurrency.amountCurrency
        }

        holder.imgIconCurrency.setImageResource(rCurrency.imgIconCurrency)
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

    class ViewHolder(itemView: View, iRecyclerView: IRecyclerView) : RecyclerView.ViewHolder(itemView)
    {
        val idCurrency: TextView
        val titleCurrencyName: TextView
        val amountCurrency: TextView
        val imgIconCurrency: ImageView
        val currencySymbol: Array<String>

        init {
            idCurrency = itemView.findViewById(R.id.idCurrency)
            titleCurrencyName = itemView.findViewById(R.id.titleCurrencyName)
            amountCurrency = itemView.findViewById(R.id.amountCurrency)
            imgIconCurrency = itemView.findViewById(R.id.imgIconCurrency)
            currencySymbol = itemView.getResources().getStringArray(R.array.list_currency_symbol)

            itemView.setOnClickListener()
            {
                val pos: Int = adapterPosition

                if (pos != RecyclerView.NO_POSITION) {
                    iRecyclerView.onItemClick(pos)
                }
            }

            /*
            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    if (iRecyclerView != null){
                        int pos = getAdapterPosition()

                        if (pos != RecyclerView.NO_POSITION) {
                            iRecyclerView.onItemLongClick(pos)
                        }
                    }
                    return true
                }
            })
             */
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