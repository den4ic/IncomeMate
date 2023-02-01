package com.genesiseternity.incomemate.wallet

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.CurrencyConverter
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import javax.inject.Inject

class WalletViewModel @Inject constructor(
    application: Application,
    currencyDetailsDao: CurrencyDetailsDao,
    currencySettingsDao: CurrencySettingsDao,
    currencyConverter: dagger.Lazy<CurrencyConverter>
) : ViewModel(), IRecyclerView
{
    private val walletRepository: WalletRepository
    private val _currencyAccountRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyAccountRecyclerModel>>
    private val _notifyItemAdapterLiveData: MutableLiveData<Int>
    private val _currencyCbtModelLiveData: MutableLiveData<String>

    val currencyAccountRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyAccountRecyclerModel>> get() = _currencyAccountRecyclerModelLiveData
    val notifyItemAdapterLiveData: MutableLiveData<Int> get() = _notifyItemAdapterLiveData
    val currencyCbtModelLiveData: MutableLiveData<String> get() = _currencyCbtModelLiveData

    init {
        walletRepository = WalletRepository(application, currencyDetailsDao, currencySettingsDao, currencyConverter)
        _currencyAccountRecyclerModelLiveData = walletRepository.currencyAccountRecyclerModelLiveData
        _notifyItemAdapterLiveData = walletRepository.notifyItemAdapterLiveData
        _currencyCbtModelLiveData = walletRepository.currencyCbtModelLiveData

        val currencySymbol: Array<String> = application.resources.getStringArray(R.array.list_currency_symbol)
        val listCurrency: Array<String> = application.resources.getStringArray(R.array.list_currency)

        walletRepository.initRecyclerCurrency(currencySymbol, listCurrency)
    }

    fun updateCurrentCurrency(currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel>): String = walletRepository.updateTotalCashAccount(currencyAccountRecyclerModel)

    override fun onCleared() {
        walletRepository.compositeDisposable.dispose()
        super.onCleared()
    }

    override fun onItemClick(pos: Int) {
        walletRepository.onItemClick(pos)
    }
}