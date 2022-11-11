package com.genesiseternity.incomemate.history

import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerModel
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerViewAdapter
import com.genesiseternity.incomemate.wallet.IRecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryRecyclerViewAdapter(context: Context, private val iRecyclerView: IRecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var inflater: LayoutInflater
    private var historyRecyclerModels: List<HistoryRecyclerModel> = emptyList()

    //fun getHistoryRecyclerModel(): List<HistoryRecyclerModel> = historyRecyclerModels

    private val TYPE_VIEW_OPERATION: Int = 1
    private val TYPE_VIEW_DATE: Int = 2

    init {
        //historyRecyclerModels = ArrayList()
        this.inflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_VIEW_OPERATION)
        {
            val view: View = inflater.inflate(R.layout.row_history_item, parent, false)
            return OperationViewHolder(view, iRecyclerView)
        }
        else
        {
            val view: View = inflater.inflate(R.layout.row_history_date_item, parent, false)
            return DateViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (TextUtils.isEmpty(historyRecyclerModels[position].date))
        {
            return TYPE_VIEW_OPERATION
        }
        else
        {
            return TYPE_VIEW_DATE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyRecyclerModel: HistoryRecyclerModel = historyRecyclerModels[position]

        if (getItemViewType(position) == TYPE_VIEW_OPERATION)
        {
            (holder as OperationViewHolder).setOperation(historyRecyclerModel)
        }
        else
        {
            (holder as DateViewHolder).setDate(historyRecyclerModel)
        }
    }

    override fun getItemCount(): Int {
        return historyRecyclerModels.size
    }

    fun setHistoryRecyclerModel(historyRecyclerModels: List<HistoryRecyclerModel>)
    {
        val diffCallBack = HistoryRecyclerModelDiffUtil(this.historyRecyclerModels, historyRecyclerModels)
        val diffRes = DiffUtil.calculateDiff(diffCallBack)
        this.historyRecyclerModels = historyRecyclerModels
        diffRes.dispatchUpdatesTo(this)

        //this.historyRecyclerModels = historyRecyclerModels
        //notifyDataSetChanged()
    }

    class OperationViewHolder(itemView: View, iRecyclerView: IRecyclerView) : RecyclerView.ViewHolder(itemView)
    {
        private var amountCash: TextView
        private var titleCategoryName: TextView
        private var titleWalletName: TextView
        private var iconCategory: ImageView
        private var iconWallet: ImageView
        private var imageCategoryType: TypedArray

        init {
            amountCash = itemView.findViewById(R.id.historyAmountCash)
            titleCategoryName = itemView.findViewById(R.id.historyTitleCategoryName)
            titleWalletName = itemView.findViewById(R.id.historyTitleWalletName)
            iconCategory = itemView.findViewById(R.id.historyIconCategory)
            iconWallet = itemView.findViewById(R.id.historyIconWallet)
            imageCategoryType = itemView.resources.obtainTypedArray(R.array.image_category_type)

            itemView.setOnClickListener()
            {
                val pos: Int = adapterPosition

                if (pos != RecyclerView.NO_POSITION)
                {
                    iRecyclerView.onItemClick(pos)
                }
            }
        }

        fun setOperation(historyRecyclerModel: HistoryRecyclerModel)
        {
            amountCash.text = historyRecyclerModel.amountCash
            titleCategoryName.text = historyRecyclerModel.titleCategoryName
            titleWalletName.text = historyRecyclerModel.titleWalletName
            iconCategory.setImageResource(imageCategoryType.getResourceId(historyRecyclerModel.iconCategory,0 ))
            iconWallet.setImageResource(historyRecyclerModel.iconWallet)
            iconCategory.setBackgroundColor(historyRecyclerModel.colorIdCategory)
            iconWallet.setBackgroundColor(historyRecyclerModel.colorIdWallet)
        }
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val dateDay: TextView
        private val dateDayOfWeek: TextView
        private val dateMonthYear: TextView
        private val amountCash: TextView

        init {
            dateDay = itemView.findViewById(R.id.historyDateDay)
            dateDayOfWeek = itemView.findViewById(R.id.historyDateDayOfWeek)
            dateMonthYear = itemView.findViewById(R.id.historyDateMonthYear)
            amountCash = itemView.findViewById(R.id.historyDateAmountCash)
        }

        fun setDate(historyRecyclerModel: HistoryRecyclerModel)
        {
            amountCash.text = historyRecyclerModel.amountCash

            val dateFormatDay: DateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val dateFormatDayOfWeek: DateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            val dateFormatMonthYear: DateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

            val calendar: Calendar = Calendar.getInstance()
            calendar.clear()
            calendar.add(Calendar.DATE, Integer.parseInt(historyRecyclerModel.date))

            dateDay.text = dateFormatDay.format(calendar.time)
            dateDayOfWeek.text = dateFormatDayOfWeek.format(calendar.time)
            dateMonthYear.text = dateFormatMonthYear.format(calendar.time)
        }
    }

    class HistoryRecyclerModelDiffUtil(
        private val oldList: List<HistoryRecyclerModel>,
        private val newList: List<HistoryRecyclerModel>) : DiffUtil.Callback()
    {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldHistoryRecyclerModel = oldList[oldItemPosition]
            val newHistoryRecyclerModel = newList[newItemPosition]
            return oldHistoryRecyclerModel.date == newHistoryRecyclerModel.date
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldHistoryRecyclerModel = oldList[oldItemPosition]
            val newHistoryRecyclerModel = newList[newItemPosition]
            return oldHistoryRecyclerModel == newHistoryRecyclerModel
        }
    }
}