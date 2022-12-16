package com.genesiseternity.incomemate.wallet

import android.app.Application
import android.content.Intent
import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WalletViewModel @Inject constructor(
    application: Application,
    currencyDetailsDao: CurrencyDetailsDao,
    currencyFormat: CurrencyFormat,
    currencyCbrRepository: CurrencyCbrRepository
    //private val currencyCbrRepository: CurrencyCbrRepository
) : ViewModel(), IRecyclerView {

    //@Inject lateinit var currencyDetailsDao: CurrencyDetailsDao
    //@Inject lateinit var currencyFormat: CurrencyFormat

    private val walletRepository: WalletRepository
    private val currencyRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyRecyclerModel>>
    private val notifyItemAdapterLiveData: MutableLiveData<Int>

    init {
        walletRepository = WalletRepository(application, currencyDetailsDao, currencyFormat, currencyCbrRepository)
        currencyRecyclerModelLiveData = walletRepository.getCurrencyRecyclerModelLiveData()
        notifyItemAdapterLiveData = walletRepository.getNotifyItemAdapterLiveData()
    }

    fun getCurrencyRecyclerModelLiveData(): MutableLiveData<ArrayList<CurrencyRecyclerModel>> = currencyRecyclerModelLiveData
    fun getNotifyItemAdapterLiveData(): MutableLiveData<Int> = notifyItemAdapterLiveData
    fun getAllAmountCurrency(): String = walletRepository.getAllAmountCurrency()

    fun initRecyclerCurrency(
        currencySymbol: Array<String>,
        imageCurrencyType: TypedArray,
        defaultCurrencyType: Int,
        listCurrency: Array<String>,
        amountCurrency: Array<String>
    ) {
        walletRepository.initRecyclerCurrency(
            currencySymbol,
            imageCurrencyType,
            defaultCurrencyType,
            listCurrency,
            amountCurrency)
    }

    override fun onCleared() {
        walletRepository.getCompositeDisposable().dispose()
        super.onCleared()
    }

    override fun onItemClick(pos: Int) {
        walletRepository.onItemClick(pos)
    }
}