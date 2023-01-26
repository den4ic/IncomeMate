package com.genesiseternity.incomemate.history

import android.content.res.TypedArray
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.databinding.RowHistoryDateItemBinding
import com.genesiseternity.incomemate.databinding.RowHistoryItemBinding
import com.genesiseternity.incomemate.wallet.IRecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HistoryRecyclerViewAdapter(private val iRecyclerView: IRecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var historyRecyclerModels: List<HistoryRecyclerModel> = emptyList()
    //fun getHistoryRecyclerModel(): List<HistoryRecyclerModel> = historyRecyclerModels

    private val TYPE_VIEW_OPERATION: Int = 1
    private val TYPE_VIEW_DATE: Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)

        if (viewType == TYPE_VIEW_OPERATION)
        {
            val bindingHistoryItem: RowHistoryItemBinding = RowHistoryItemBinding.inflate(inflater, parent, false)
            return OperationViewHolder(bindingHistoryItem, iRecyclerView)
        }
        else
        {
            val bindingHistoryDate: RowHistoryDateItemBinding = RowHistoryDateItemBinding.inflate(inflater, parent, false)
            return DateViewHolder(bindingHistoryDate)
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

    class OperationViewHolder(binding: RowHistoryItemBinding, iRecyclerView: IRecyclerView) : RecyclerView.ViewHolder(binding.root)
    {
        private val amountCash: TextView
        private val titleCategoryName: TextView
        private val titleWalletName: TextView
        private val iconCategory: ImageView
        private val iconWallet: ImageView
        private val imageCategoryType: TypedArray
        private val imageWalletType: TypedArray

        private val bindingRoot = binding.root

        init {
            amountCash = binding.historyAmountCash
            titleCategoryName = binding.historyTitleCategoryName
            titleWalletName = binding.historyTitleWalletName
            iconCategory = binding.historyIconCategory
            iconWallet = binding.historyIconWallet
            imageCategoryType = bindingRoot.resources.obtainTypedArray(R.array.image_category_type)
            imageWalletType = bindingRoot.resources.obtainTypedArray(R.array.image_currency_type)

            bindingRoot.setOnClickListener()
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
            iconWallet.setImageResource(imageWalletType.getResourceId(historyRecyclerModel.iconWallet,0 ))
            iconCategory.setBackgroundColor(historyRecyclerModel.colorIdCategory)
            iconWallet.setBackgroundColor(historyRecyclerModel.colorIdWallet)
        }
    }

    class DateViewHolder(binding: RowHistoryDateItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        private val dateDay: TextView
        private val dateDayOfWeek: TextView
        private val dateMonthYear: TextView
        private val amountCash: TextView

        init {
            dateDay = binding.historyDateDay
            dateDayOfWeek = binding.historyDateDayOfWeek
            dateMonthYear = binding.historyDateMonthYear
            amountCash = binding.historyDateAmountCash
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