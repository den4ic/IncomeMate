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
    private var currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel> = ArrayList()

    private lateinit var currencySymbol: Array<String>
    private var defaultCurrencyType: Int = 0
    private lateinit var listCurrency: Array<String>

    private val textAddAccount: String = "Добавить счет"
    private val textDefaultCurrency: String = "Валюта по умолчанию - "
    private val textNewAccount: String = "Новый счет №"

    private val _currencyAccountRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyAccountRecyclerModel>> = MutableLiveData()
    private val _notifyItemAdapterLiveData: MutableLiveData<Int> = MutableLiveData()
    private val _currencyCbtModelLiveData: MutableLiveData<String> = MutableLiveData()
    private val _compositeDisposable: CompositeDisposable = CompositeDisposable()

    val currencyAccountRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyAccountRecyclerModel>> get() = _currencyAccountRecyclerModelLiveData
    val notifyItemAdapterLiveData: MutableLiveData<Int> get() = _notifyItemAdapterLiveData
    val currencyCbtModelLiveData: MutableLiveData<String> get() = _currencyCbtModelLiveData
    val compositeDisposable: CompositeDisposable get() = _compositeDisposable

    fun initRecyclerCurrency(currencySymbol: Array<String>, listCurrency: Array<String>)
    {
        this.currencySymbol = currencySymbol
        this.listCurrency = listCurrency

        _compositeDisposable.add(currencySettingsDao.getDefaultCurrencyByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { defaultCurrencyType = it }, {} ))

        //_compositeDisposable.add(currencyDetailsDao.getAll()
        _compositeDisposable.add(currencyDetailsDao.getAllSortedCurrencyData()
            .filter {
                if (it.isNotEmpty()) {
                    return@filter true
                } else {
                    _compositeDisposable.add(currencyDetailsDao.insertCurrencyData(*createCurrencyRecycler().toTypedArray())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            _currencyAccountRecyclerModelLiveData.value = currencyAccountRecyclerModel
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

                    currencyAccountRecyclerModel.add(CurrencyAccountRecyclerModel(
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
                    currencyConverter.get().updateAllCurrencyAmount(::setTotalCashAccount, currencyAccountRecyclerModel)

                    createCurrencyAddingRecycler(currencySymbol)
                    _currencyAccountRecyclerModelLiveData.value = currencyAccountRecyclerModel
                }
            ))
    }

    fun updateTotalCashAccount(currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel>): String = currencyConverter.get().updateTotalCashAccount(currencyAccountRecyclerModel)

    private fun setTotalCashAccount(res: String) { _currencyCbtModelLiveData.value = res }

    private fun createCurrencyRecycler(): ArrayList<CurrencyDetailsEntity>
    {
        for (i in listCurrency.indices)
        {
            currencyAccountRecyclerModel.add(CurrencyAccountRecyclerModel(
                i,
                listCurrency[i],
                "0",
                defaultCurrencyType,
                0,
                0))
        }

        val currencyDetailsEntities: ArrayList<CurrencyDetailsEntity> = ArrayList(currencyAccountRecyclerModel.size)

        for (i in currencyAccountRecyclerModel.indices)
        {
            val currencyDetailsEntity: CurrencyDetailsEntity = CurrencyDetailsEntity(
                currencyAccountRecyclerModel[i].idCurrency,
                currencyAccountRecyclerModel[i].titleCurrencyName,
                currencyAccountRecyclerModel[i].amountCurrency + " " + currencySymbol[defaultCurrencyType],
                0,0,0
            )
            currencyDetailsEntities.add(currencyDetailsEntity)
        }

        return currencyDetailsEntities
    }

    private fun createCurrencyAddingRecycler(currencySymbol: Array<String>)
    {
        currencyAccountRecyclerModel.add(CurrencyAccountRecyclerModel(
            -1,
            textAddAccount,
            textDefaultCurrency + currencySymbol[defaultCurrencyType],
            defaultCurrencyType,
            -1,
            0))
    }

    fun onItemClick(pos: Int)
    {
        val insertIndex: Int = currencyAccountRecyclerModel.size-1

        if (pos != insertIndex)
        {
            goToSelectedActivity(pos)
        }
        else
        {
            var checkRemovedCurrencyRecycler: Boolean = false
            var missingIndex: Int = 0

            for (i in currencyAccountRecyclerModel.indices)
            {
                if (i < currencyAccountRecyclerModel[i].idCurrency)
                {
                    checkRemovedCurrencyRecycler = true
                    missingIndex = i
                    break
                }
            }

            if (checkRemovedCurrencyRecycler)
            {
                currencyAccountRecyclerModel.add(missingIndex, CurrencyAccountRecyclerModel(
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
                currencyAccountRecyclerModel.add(insertIndex, CurrencyAccountRecyclerModel(
                    pos,
                    textNewAccount + ++numAccount,
                    "0",
                    defaultCurrencyType,
                    0,
                    0))

                _notifyItemAdapterLiveData.value = insertIndex
                goToSelectedActivity(insertIndex)
            }
        }
    }

    private fun goToSelectedActivity(idPos: Int)
    {
        val intent: Intent = Intent(application, CurrencyAccountActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtra("idCurrency", currencyAccountRecyclerModel[idPos].idCurrency)
        intent.putExtra("editTextCurrency", currencyAccountRecyclerModel[idPos].titleCurrencyName)
        intent.putExtra("editTextAmountCurrency", currencyAccountRecyclerModel[idPos].amountCurrency)
        intent.putExtra("imageViewCurrencyOne", currencyAccountRecyclerModel[idPos].imgIconCurrency)
        intent.putExtra("defaultCurrencyType", currencyAccountRecyclerModel[idPos].currencyType)

        currencyAccountRecyclerModel[idPos].currencyType = defaultCurrencyType
        application.startActivity(intent)
    }
}