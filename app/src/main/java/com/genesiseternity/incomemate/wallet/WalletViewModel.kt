package com.genesiseternity.incomemate.wallet

import android.app.Application
import android.content.Intent
import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.CurrencyConverter
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WalletViewModel @Inject constructor(
    application: Application,
    currencyDetailsDao: CurrencyDetailsDao,
    currencySettingsDao: CurrencySettingsDao,
    currencyConverter: dagger.Lazy<CurrencyConverter>
) : ViewModel(), IRecyclerView {

    private val walletRepository: WalletRepository
    private val currencyRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyRecyclerModel>>
    private val notifyItemAdapterLiveData: MutableLiveData<Int>
    private val currencyCbtModelLiveData: MutableLiveData<String>

    init {
        walletRepository = WalletRepository(application, currencyDetailsDao, currencySettingsDao, currencyConverter)
        currencyRecyclerModelLiveData = walletRepository.getCurrencyRecyclerModelLiveData()
        notifyItemAdapterLiveData = walletRepository.getNotifyItemAdapterLiveData()
        currencyCbtModelLiveData = walletRepository.getCurrencyCbtModelLiveData()

        val currencySymbol: Array<String> = application.resources.getStringArray(R.array.list_currency_symbol)
        val listCurrency: Array<String> = application.resources.getStringArray(R.array.list_currency)
        val amountCurrency: Array<String> = application.resources.getStringArray(R.array.list_amount_currency)

        walletRepository.initRecyclerCurrency(
            currencySymbol,
            listCurrency,
            amountCurrency)
    }

    fun getCurrencyRecyclerModelLiveData(): MutableLiveData<ArrayList<CurrencyRecyclerModel>> = currencyRecyclerModelLiveData
    fun getNotifyItemAdapterLiveData(): MutableLiveData<Int> = notifyItemAdapterLiveData
    fun getCurrencyCbtModelLiveData(): MutableLiveData<String> = currencyCbtModelLiveData
    fun updateCurrentCurrency(currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>): String = walletRepository.updateTotalCashAccount(currencyRecyclerModel)

    override fun onCleared() {
        walletRepository.getCompositeDisposable().dispose()
        super.onCleared()
    }

    override fun onItemClick(pos: Int) {
        walletRepository.onItemClick(pos)
    }
}