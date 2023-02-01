package com.genesiseternity.incomemate

import android.util.Log
import com.genesiseternity.incomemate.retrofit.CurrencyBodyModel
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.utils.replaceToRegex
import com.genesiseternity.incomemate.wallet.CurrencyAccountRecyclerModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CurrencyConverter @Inject constructor(
    private val currencyCbrRepository: CurrencyCbrRepository,
    private val currencyFormat: CurrencyFormat
)
{
    var defaultCurrencyType: Int = 0
    var currencySymbol: Array<String> = emptyArray()

    private lateinit var cbrCurrencyList: ArrayList<CurrencyBodyModel>
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun updateTotalCashAccount(currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel>): String
    {
        var amountCurrency: Float = 0.0f

        for (i in 0 until currencyAccountRecyclerModel.size)
        {
            //"[^0-9]"
            val tempAmountCurrency: String = currencyAccountRecyclerModel[i].amountCurrency.replaceToRegex()

            if (tempAmountCurrency.isNotEmpty() && currencyAccountRecyclerModel[i].idCurrency != -1)
            {
                if (currencyAccountRecyclerModel[i].currencyType != 0)
                {
                    for (j in cbrCurrencyList.indices)
                    {
                        if (currencyAccountRecyclerModel[i].currencyType == j)
                        {
                            //amountCurrency += tempAmountCurrency.toFloat() * cbrCurrencyList[j]
                            amountCurrency += tempAmountCurrency.toFloat() * cbrCurrencyList[j].value
                        }
                    }
                }
                else
                {
                    amountCurrency += tempAmountCurrency.toFloat()
                }
            }
        }

        amountCurrency /= cbrCurrencyList[defaultCurrencyType].value / cbrCurrencyList[defaultCurrencyType].nominal

        return if (amountCurrency != 0.0f) currencyFormat.setStringTextFormatted(amountCurrency.toString()) + " " +  currencySymbol[defaultCurrencyType] else "0"
    }

    fun updateAllCurrencyAmount(action: (res: String) -> Unit, currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel>) : CompositeDisposable
    {
        compositeDisposable.add(currencyCbrRepository.getLastCurrencyDate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    cbrCurrencyList = arrayListOf(
                        CurrencyBodyModel("rub", 1.0f, 1),
                        it.currencyList.usd,
                        it.currencyList.eur,
                        it.currencyList.aed,
                        it.currencyList.cny,
                        it.currencyList.aud,
                        it.currencyList.gbp,
                        it.currencyList.cad,
                        it.currencyList.chf,
                        it.currencyList.jpy
                    )

                    action(updateTotalCashAccount(currencyAccountRecyclerModel))
                },
                {
                    it.printStackTrace()
                    // Log.d("WalletViewModel", "Невозможно получить данные")

                    // TODO("implement the preservation of current currency values during the
                    //  initial connection to the network or set static data
                    //  with periodic checks when accessing the network")

                    cbrCurrencyList = arrayListOf(
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1),
                        CurrencyBodyModel("", 1.0f, 1)
                    )

                    action(updateTotalCashAccount(currencyAccountRecyclerModel))
                }
            ))

        return compositeDisposable
    }

}