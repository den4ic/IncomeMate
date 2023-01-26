package com.genesiseternity.incomemate

import android.util.Log
import com.genesiseternity.incomemate.retrofit.CurrencyBodyModel
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.utils.isNumeric
import com.genesiseternity.incomemate.utils.replaceToRegex
import com.genesiseternity.incomemate.wallet.CurrencyRecyclerModel
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

    fun updateTotalCashAccount(currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>): String
    {
        var amountCurrency: Float = 0.0f

        //if (currencyRecyclerModel[currencyRecyclerModel.lastIndex].idCurrency != -1)
        //{
        //    return amountCurrency.toString()
        //}

        for (i in 0 until currencyRecyclerModel.size) /////////// -1 fro wallet
        {
            //"[^0-9]"
            val tempAmountCurrency: String = currencyRecyclerModel[i].amountCurrency.replaceToRegex()


            //if (tempAmountCurrency.isNotEmpty() && currencyRecyclerModel[i].idCurrency != -1)
            //if (tempAmountCurrency.isNotEmpty() && String.isNumeric(tempAmountCurrency))
            //if (tempAmountCurrency.isNotEmpty())
            if (tempAmountCurrency.isNotEmpty() && currencyRecyclerModel[i].idCurrency != -1)
            {
                if (currencyRecyclerModel[i].currencyType != 0)
                {
                    for (j in cbrCurrencyList.indices)
                    {
                        if (currencyRecyclerModel[i].currencyType == j)
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

    fun updateAllCurrencyAmount(action: (res: String) -> Unit, currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>) : CompositeDisposable
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

                    action(updateTotalCashAccount(currencyRecyclerModel))
                },
                {
                    it.printStackTrace()
                    Log.d("WalletViewModel", "Невозможно получить данные")

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

                    action(updateTotalCashAccount(currencyRecyclerModel))
                }
            ))

        return compositeDisposable
    }

}