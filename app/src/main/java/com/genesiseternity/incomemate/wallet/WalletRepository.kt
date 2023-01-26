package com.genesiseternity.incomemate.wallet

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.genesiseternity.incomemate.CurrencyConverter
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WalletRepository (
    private val application: Application,
    private val currencyDetailsDao: CurrencyDetailsDao,
    private val currencySettingsDao: CurrencySettingsDao,
    private val currencyConverter: dagger.Lazy<CurrencyConverter>
    ) {

    private val currencyRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyRecyclerModel>> = MutableLiveData()
    private val notifyItemAdapterLiveData: MutableLiveData<Int> = MutableLiveData()

    private var currencyRecyclerModel: ArrayList<CurrencyRecyclerModel> = ArrayList()

    private lateinit var currencySymbol: Array<String>
    private var defaultCurrencyType: Int = 0
    private lateinit var listCurrency: Array<String>
    private lateinit var amountCurrency: Array<String>

    private val currencyCbtModelLiveData: MutableLiveData<String> = MutableLiveData()

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val textAddAccount: String = "Добавить счет"
    private val textDefaultCurrency: String = "Валюта по умолчанию - "
    private val textNewAccount: String = "Новый счет №"

    fun getCurrencyRecyclerModelLiveData(): MutableLiveData<ArrayList<CurrencyRecyclerModel>> = currencyRecyclerModelLiveData
    fun getNotifyItemAdapterLiveData(): MutableLiveData<Int> = notifyItemAdapterLiveData
    fun getCurrencyCbtModelLiveData(): MutableLiveData<String> = currencyCbtModelLiveData
    fun getCompositeDisposable(): CompositeDisposable = compositeDisposable

    fun initRecyclerCurrency(
        currencySymbol: Array<String>,
        listCurrency: Array<String>,
        amountCurrency: Array<String>
    ) {
        this.currencySymbol = currencySymbol
        this.listCurrency = listCurrency
        this.amountCurrency = amountCurrency

        compositeDisposable.add(currencySettingsDao.getDefaultCurrencyByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { defaultCurrencyType = it }, {} ))

        //compositeDisposable.add(currencyDetailsDao.getAll()
        compositeDisposable.add(currencyDetailsDao.getAllSortedCurrencyData()
            .filter {
                if (it.isNotEmpty()) {
                    return@filter true
                } else {
                    compositeDisposable.add(currencyDetailsDao.insertCurrencyData(*createCurrencyRecycler().toTypedArray())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            currencyRecyclerModelLiveData.value = currencyRecyclerModel
                        })
                    return@filter false
                }
            }
            .flatMapObservable { t -> Observable.fromIterable(t) }
            //.flatMapIterable { it }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    currencyDetailsEntities ->

                    currencyRecyclerModel.add(CurrencyRecyclerModel(
                        currencyDetailsEntities.id,
                        currencyDetailsEntities.titleCurrency,
                        currencyDetailsEntities.amountCurrency,
                        currencyDetailsEntities.currencyType,
                        currencyDetailsEntities.idCardViewIcon,
                        currencyDetailsEntities.idColorIcon
                    ))
                },
                {
                    it.printStackTrace()
                    Log.d("WalletViewModel", " = " + it.localizedMessage)
                },
                {
                    currencyConverter.get().defaultCurrencyType = defaultCurrencyType
                    currencyConverter.get().currencySymbol = currencySymbol
                    currencyConverter.get().updateAllCurrencyAmount(::setTotalCashAccount, currencyRecyclerModel)

                    createCurrencyAddingRecycler(currencySymbol)
                    currencyRecyclerModelLiveData.value = currencyRecyclerModel
                }
            ))

        //data is loading
        /*
        Completable.fromAction(() -> currencyDetailsDao.get().insertCurrencyData(currencyDetailsEntities))
            //.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new CompletableObserver()
         */
    }

    fun updateTotalCashAccount(currencyRecyclerModel: ArrayList<CurrencyRecyclerModel>): String = currencyConverter.get().updateTotalCashAccount(currencyRecyclerModel)

    private fun setTotalCashAccount(res: String) { currencyCbtModelLiveData.value = res }

    private fun createCurrencyRecycler(): ArrayList<CurrencyDetailsEntity>
    {
        for (i in listCurrency.indices)
        {
            currencyRecyclerModel.add(CurrencyRecyclerModel(
                i,
                listCurrency[i],
                amountCurrency[i],
                defaultCurrencyType,
                0,
                0))
        }

        val currencyDetailsEntities: ArrayList<CurrencyDetailsEntity> = ArrayList(currencyRecyclerModel.size)

        for (i in currencyRecyclerModel.indices)
        {
            val currencyDetailsEntity: CurrencyDetailsEntity = CurrencyDetailsEntity(
                currencyRecyclerModel[i].idCurrency,
                currencyRecyclerModel[i].titleCurrencyName,
                currencyRecyclerModel[i].amountCurrency + " " + currencySymbol[defaultCurrencyType],
                0,0,0
            )
            currencyDetailsEntities.add(currencyDetailsEntity)
        }

        return currencyDetailsEntities
    }

    private fun createCurrencyAddingRecycler(currencySymbol: Array<String>)
    {
        currencyRecyclerModel.add(CurrencyRecyclerModel(
            -1,
            textAddAccount,
            textDefaultCurrency + currencySymbol[defaultCurrencyType],
            defaultCurrencyType,
            -1,
            0))
    }

    fun onItemClick(pos: Int)
    {
        val insertIndex: Int = currencyRecyclerModel.size-1

        if (pos != insertIndex)
        {
            goToSelectedActivity(pos)
        }
        else
        {
            var checkRemovedCurrencyRecycler: Boolean = false
            var missingIndex: Int = 0

            for (i in currencyRecyclerModel.indices)
            {
                if (i < currencyRecyclerModel[i].idCurrency)
                {
                    checkRemovedCurrencyRecycler = true
                    missingIndex = i
                    break
                }
            }

            if (checkRemovedCurrencyRecycler)
            {
                currencyRecyclerModel.add(missingIndex, CurrencyRecyclerModel(
                    missingIndex,
                    textNewAccount + missingIndex,
                    "0",
                    defaultCurrencyType,
                    0,
                    0))

                goToSelectedActivity(missingIndex)
            }
            else
            {
                var numAccount: Int = insertIndex
                currencyRecyclerModel.add(insertIndex, CurrencyRecyclerModel(
                    pos,
                    textNewAccount + ++numAccount,
                    "0",
                    defaultCurrencyType,
                    0,
                    0))

                notifyItemAdapterLiveData.value = insertIndex
                goToSelectedActivity(insertIndex)
            }
        }
    }

    private fun goToSelectedActivity(idPos: Int)
    {
        val intent: Intent = Intent(application, CurrencyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtra("idCurrency", currencyRecyclerModel[idPos].idCurrency)
        intent.putExtra("editTextCurrency", currencyRecyclerModel[idPos].titleCurrencyName)
        intent.putExtra("editTextAmountCurrency", currencyRecyclerModel[idPos].amountCurrency)
        intent.putExtra("imageViewCurrencyOne", currencyRecyclerModel[idPos].imgIconCurrency)
        intent.putExtra("defaultCurrencyType", currencyRecyclerModel[idPos].currencyType)

        currencyRecyclerModel[idPos].currencyType = defaultCurrencyType
        application.startActivity(intent)
    }
}