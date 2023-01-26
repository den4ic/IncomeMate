package com.genesiseternity.incomemate.wallet

import android.app.AlertDialog
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.ViewModelProviderFactory
import com.genesiseternity.incomemate.databinding.FragmentWalletBinding
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class WalletFragment : DaggerFragment() {
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>

    private lateinit var walletViewModel: WalletViewModel

    private lateinit var binding: FragmentWalletBinding
    private lateinit var adapter: CurrencyRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewTotalCashAccount: TextView

    private lateinit var deletedCurrencyRecyclerModel: CurrencyRecyclerModel
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val alertSwipeTitle: String = "Удалить счёт - "
    private val alertSwipeMessage: String = "Все операции связанные с данным счётом будут безвозвратно удалены.\n\nБаланс других счетов не поменяется."
    private val alertSwipePositive: String = "Удалить"
    private val alertSwipeNegative: String = "Отменить"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        val view: View = binding.root

        walletViewModel = ViewModelProvider(this, providerFactory)[WalletViewModel::class.java]

        recyclerView = binding.recyclerViewList
        textViewTotalCashAccount = binding.textViewTotalCashAccount

        adapter = CurrencyRecyclerViewAdapter(walletViewModel)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = object : LinearLayoutManager(context){ override fun canScrollVertically(): Boolean { return false } }

        val itemDecorator = DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.row_item_divider_cut)!!)
        recyclerView.addItemDecoration(itemDecorator)

        walletViewModel.getCurrencyRecyclerModelLiveData().observe(viewLifecycleOwner) { currencyRecyclerModels ->
            adapter.setDataCurrencyRecyclerModel(currencyRecyclerModels)
            //textViewTotalCashAccount.text = walletViewModel.getAllAmountCurrency()
        }

        walletViewModel.getCurrencyCbtModelLiveData().observe(viewLifecycleOwner) {
            textViewTotalCashAccount.text = it
        }

        walletViewModel.getNotifyItemAdapterLiveData().observe(viewLifecycleOwner) { adapter.notifyItemChanged(it) }

        initRecyclerTouch()

        return view
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    //override fun onDestroyView() {
    //    super.onDestroyView()
    //    binding = null
    //}


    //region DrawAndTouchRecycler
    private fun initRecyclerTouch()
    {
        val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val itemTouchHelperSwipe: ItemTouchHelper = ItemTouchHelper(simpleSwipeCallback)
        itemTouchHelperSwipe.attachToRecyclerView(recyclerView)
    }

    val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0)
    {

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if(actionState == ItemTouchHelper.ACTION_STATE_DRAG)
            {
                viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#dfe6e9"))
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white))
        }


        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val insertIndex: Int = adapter.itemCount -1

            if (viewHolder.getLayoutPosition() != insertIndex)
            {
                val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                //int swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, 0)
            }
            return makeMovementFlags(0, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos: Int = viewHolder.getAdapterPosition()
            val toPos: Int = target.getAdapterPosition()

            val insertIndex: Int = adapter.itemCount -1

            if (fromPos != insertIndex && toPos != insertIndex)
            {
                Collections.swap(adapter.getCurrencyRecyclerModel(), fromPos, toPos)
                recyclerView.adapter?.notifyItemMoved(fromPos, toPos)
            }

            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    }

    val simpleSwipeCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val insertIndex: Int = adapter.itemCount - 1

            if (viewHolder.layoutPosition != insertIndex) {
                setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        private fun setDeleteIcon(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val clearPaint: Paint = Paint()
            clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            val background: ColorDrawable = ColorDrawable()
            val backgroundColor: Int = Color.parseColor("#d63031")

            val deleteDrawable: Drawable = ContextCompat.getDrawable(view!!.context, R.drawable.ic_baseline_delete_white_24)!!
            val intrinsicWidth: Int = deleteDrawable.intrinsicWidth
            val intrinsicHeight: Int = deleteDrawable.intrinsicHeight

            val itemView: View = viewHolder.itemView
            val itemHeight: Int = itemView.height

            val isCanelled: Boolean = (dX == 0.0f && !isCurrentlyActive)

            if (isCanelled) {
                c.drawRect(
                    itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(),
                    itemView.bottom.toFloat(), clearPaint
                )
                return
            }

            background.color = backgroundColor
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)

            val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin: Int = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight: Int = itemView.right - deleteIconMargin
            val deleteIconBottom: Int = deleteIconTop + intrinsicHeight

            deleteDrawable.setBounds(
                deleteIconLeft,
                deleteIconTop,
                deleteIconRight,
                deleteIconBottom
            )
            deleteDrawable.draw(c)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position: Int = viewHolder.adapterPosition
            val insertIndex: Int = adapter.itemCount - 1

            if (position != insertIndex) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                //builder.setTitle("Удалить счёт - " + currencyRecyclerModel.get(position).getTitleCurrencyName() + " ?")
                builder.setTitle(
                    alertSwipeTitle + adapter.getCurrencyRecyclerModel()[position].titleCurrencyName + " ?"
                )
                builder.setCancelable(true)
                builder.setMessage(alertSwipeMessage)

                builder.setPositiveButton(alertSwipePositive) { dialogInterface, which ->
                    when (direction) {
                        ItemTouchHelper.LEFT ->
                            compositeDisposable.add(currencyDetailsDao.get().deleteCurrentCurrencyData(adapter.getCurrencyRecyclerModel()[position].idCurrency)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    {
                                        deletedCurrencyRecyclerModel = adapter.getCurrencyRecyclerModel()[position]
                                        adapter.deleteCurrencyRecyclerModel(deletedCurrencyRecyclerModel)

                                        //textViewTotalCashAccount.text = walletViewModel.getAllAmountCurrency()
                                        textViewTotalCashAccount.text = walletViewModel.updateCurrentCurrency(adapter.getCurrencyRecyclerModel())

                                        Snackbar.make(
                                            recyclerView,
                                            deletedCurrencyRecyclerModel.titleCurrencyName,
                                            Snackbar.LENGTH_LONG
                                        ).setAction(alertSwipeNegative) {
                                            adapter.addCurrencyRecyclerModelToPos(position, deletedCurrencyRecyclerModel)

                                            compositeDisposable.add(currencyDetailsDao.get().insertCurrencyData(CurrencyDetailsEntity(
                                                    deletedCurrencyRecyclerModel.idCurrency,
                                                    deletedCurrencyRecyclerModel.titleCurrencyName,
                                                    deletedCurrencyRecyclerModel.amountCurrency,
                                                    deletedCurrencyRecyclerModel.currencyType,
                                                    //imageCurrencyType.getResourceId(deletedCurrencyRecyclerModel.getImgIconCurrency(), 0),
                                                    0,
                                                    deletedCurrencyRecyclerModel.selectedColorId
                                                )
                                            )
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(
                                                    {
                                                        //adapter.notifyItemInserted(position)

                                                        //textViewTotalCashAccount.text = walletViewModel.getAllAmountCurrency()
                                                        textViewTotalCashAccount.text = walletViewModel.updateCurrentCurrency(adapter.getCurrencyRecyclerModel())
                                                    },
                                                    {

                                                    }))
                                        }.show()
                                    },
                                    {

                                    }
                                ))

                        //recyclerCurrency.remove(position)
                        //adapter.notifyItemRemoved(position)
                        //break
                        ItemTouchHelper.RIGHT ->
                            TODO()
                        //recyclerCurrency.remove(position)
                        //adapter.notifyItemRemoved(position)
                    }
                    dialogInterface.dismiss()
                }

                builder.setNegativeButton(alertSwipeNegative) { dialogInterface, which ->
                    adapter.notifyItemChanged(position)
                    //dialogInterface.cancel()
                }

                builder.setOnCancelListener {
                    adapter.notifyItemChanged(position)
                    it.cancel()
                }

                builder.show()
            }
        }
    }
    //endregion
}