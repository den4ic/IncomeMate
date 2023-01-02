package com.genesiseternity.incomemate.wallet

import android.app.Application
import android.content.Intent
import android.content.res.TypedArray
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WalletRepository (
    private val application: Application,
    private val currencyDetailsDao: CurrencyDetailsDao,
    private val currencyFormat: CurrencyFormat,
    private val currencySettingsDao: CurrencySettingsDao,
    private var currencyCbrRepository: CurrencyCbrRepository
    ) {

    private val currencyRecyclerModelLiveData: MutableLiveData<ArrayList<CurrencyRecyclerModel>>
    private val notifyItemAdapterLiveData: MutableLiveData<Int>

    private var currencyRecyclerModel: ArrayList<CurrencyRecyclerModel> = ArrayList()

    private lateinit var currencySymbol: Array<String>
    private var defaultCurrencyType: Int = 0
    private lateinit var listCurrency: Array<String>
    private lateinit var amountCurrency: Array<String>

    //@Inject lateinit var currencyConverterCbrApi: CurrencyConverterCbrApi
    private lateinit var cbrCurrencyList: FloatArray
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val textAddAccount: String = "Добавить счет"
    private val textDefaultCurrency: String = "Валюта по умолчанию - "
    private val textNewAccount: String = "Новый счет №"

    fun getCurrencyRecyclerModelLiveData(): MutableLiveData<ArrayList<CurrencyRecyclerModel>> = currencyRecyclerModelLiveData
    fun getNotifyItemAdapterLiveData(): MutableLiveData<Int> = notifyItemAdapterLiveData
    fun getCompositeDisposable(): CompositeDisposable = compositeDisposable

    init {
        currencyRecyclerModelLiveData = MutableLiveData()
        notifyItemAdapterLiveData = MutableLiveData()
    }

    fun initRecyclerCurrency(
        currencySymbol: Array<String>,
        listCurrency: Array<String>,
        amountCurrency: Array<String>
    ) {
        this.currencySymbol = currencySymbol
        this.listCurrency = listCurrency
        this.amountCurrency = amountCurrency

        /*
        createCurrencyRecycler()

        currencyDetailsDao.getAllSortedCurrencyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.isNotEmpty()) {
                        fillCurrencyRecyclerModel(it)
                    } else {
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

                        compositeDisposable.add(currencyDetailsDao.insertCurrencyData(*currencyDetailsEntities.toTypedArray())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                createCurrencyAddingRecycler(currencySymbol)
                                currencyRecyclerModelLiveData.setValue(currencyRecyclerModel)
                            })
                    }
                },
                {
                    Log.d("WalletViewModel", " = " + it.localizedMessage)
                }
            )

         */

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
                            currencyRecyclerModelLiveData.setValue(currencyRecyclerModel)
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
                    Log.d("WalletViewModel", " = " + it.localizedMessage)
                },
                {
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

    /*
    private fun fillCurrencyRecyclerModel(currencyDetailsEntities: List<CurrencyDetailsEntity>)
    {
        for (i in currencyDetailsEntities.indices)
        {
            if (currencyDetailsEntities[i].id != 0 && currencyDetailsEntities[i].id != 1)
            {
                currencyRecyclerModel.add(CurrencyRecyclerModel(
                    currencyDetailsEntities[i].id,
                    currencyDetailsEntities[i].titleCurrency,
                    currencyDetailsEntities[i].amountCurrency,
                    currencyDetailsEntities[i].currencyType,
                    imageCurrencyType.getResourceId(currencyDetailsEntities[i].idCardViewIcon, 0),
                    currencyDetailsEntities[i].idColorIcon))
            }
            else
            {
                currencyRecyclerModel[i].idCurrency = currencyDetailsEntities[i].id
                currencyRecyclerModel[i].titleCurrencyName = currencyDetailsEntities[i].titleCurrency
                currencyRecyclerModel[i].amountCurrency = currencyDetailsEntities[i].amountCurrency
                currencyRecyclerModel[i].currencyType = currencyDetailsEntities[i].currencyType
                currencyRecyclerModel[i].imgIconCurrency = imageCurrencyType.getResourceId(currencyDetailsEntities[i].idCardViewIcon, 0)
                currencyRecyclerModel[i].selectedColorId = currencyDetailsEntities[i].idColorIcon
            }
        }

        //updateAllAmountCurrency()
        createCurrencyAddingRecycler(currencySymbol)
        currencyRecyclerModelLiveData.value = currencyRecyclerModel
    }
     */

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

                //adapter.notifyItemInserted(insertIndex)
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

    fun getAllAmountCurrency(): String
    {
        updateAllAmountCurrency()

        var amountCurrency: Float = 0.0f

        for (i in 0 until currencyRecyclerModel.size-1)
        {
            //"[^0-9]"
            val tempAmountCurrency: String = currencyRecyclerModel[i].amountCurrency.replace("[^\\d.-]".toRegex(), "")

            //if (tempAmountCurrency.isNotEmpty() && currencyRecyclerModel[i].idCurrency != -1)
            if (tempAmountCurrency.isNotEmpty())
            {
                amountCurrency += tempAmountCurrency.toFloat()
            }
        }

        return if (amountCurrency != 0.0f) currencyFormat.setStringTextFormatted(amountCurrency.toString()) + " " +  currencySymbol[defaultCurrencyType] else "0"
    }



    private lateinit var currencyCbtModelLiveData: MutableLiveData<String>

    fun getCurrencyCbtModelLiveData(): MutableLiveData<String>
    {
        return currencyCbtModelLiveData
    }

    //@Inject lateinit var currencyCbrRepository: dagger.Lazy<CurrencyCbrRepository>

    private fun updateAllAmountCurrency()
    {
        //currencyCbrRepository.get().getLastCurrencyDate()
        currencyCbrRepository.getLastCurrencyDate()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    //Log.d("WalletViewModel", "1 - " + currencyCbrModel.getDate())
                    //Log.d("WalletViewModel", "getUsd - " + currencyCbrModel.getCurrencyList().getUsd().getName() +
                    //        " " + currencyCbrModel.getCurrencyList().getUsd().getValue())
                    //Log.d("WalletViewModel", "getCny - " + currencyCbrModel.getCurrencyList().getCny().getName() +
                    //        " " + currencyCbrModel.getCurrencyList().getCny().getValue())

                    cbrCurrencyList = floatArrayOf(
                        1f, // RUB
                        it.currencyList.usd.value.toFloat(),
                        it.currencyList.eur.value.toFloat(),
                        1f, //AE
                        it.currencyList.cny.value.toFloat(),
                        it.currencyList.aud.value.toFloat(),
                        it.currencyList.gbp.value.toFloat(),
                        it.currencyList.cad.value.toFloat(),
                        it.currencyList.chf.value.toFloat(),
                        it.currencyList.jpy.value.toFloat()
                    )

                    for (i in cbrCurrencyList.indices)
                        Log.d("WalletViewModel", "getSuffix - " + cbrCurrencyList[i])

                    /*
                    float amountCurrency = 0
                    float tempCurrCurrency = 1

                    for (int i = 0 i < currencyRecyclerModel.size() i++)
                    {
                        //Log.d("WalletViewModel", "getCurrencyType - " + currencyRecyclerModel.get(i).getCurrencyType() + " " + currencySymbol[currencyRecyclerModel.get(i).getCurrencyType()])

                        for (int j = 0 j < cbrCurrencyList.size() j++)
                        {
                            if (currencyRecyclerModel.get(i).getCurrencyType() == j)
                            {
                                tempCurrCurrency = cbrCurrencyList.get(j)
                                //Log.d("WalletViewModel", "111 - " + cbrCurrencyList.get(j) + " " + currencyRecyclerModel.get(i).getTitleCurrencyName())

                                //"[^0-9]"
                                String tempAmountCurrency = currencyRecyclerModel.get(i).getAmountCurrency().replaceAll("[^\\d.-]", "")

                                if (!tempAmountCurrency.isEmpty())
                                {
                                    amountCurrency += Float.parseFloat(tempAmountCurrency) * tempCurrCurrency
                                }
                            }
                        }
                    }

                    currencyCbtModelLiveData.setValue(amountCurrency != 0 ? currencyFormat.get().setStringTextFormatted(String.valueOf(amountCurrency)) + " " +  currencySymbol[defaultCurrencyType] : "0")

                    createCurrencyAddingRecycler(currencySymbol)
                    currencyRecyclerModelLiveData.setValue(currencyRecyclerModel)

                     */
                },
                {
                    Log.d("WalletViewModel", "Невозможно получить данные")

                    var amountCurrency: Float = 0.0f

                    for (i in 0 until currencyRecyclerModel.size-1)
                    {
                        val tempAmountCurrency: String = currencyRecyclerModel[i].amountCurrency.replace("[^\\d.-]".toRegex(), "")

                        if (tempAmountCurrency.isNotEmpty())
                        {
                            amountCurrency += tempAmountCurrency.toFloat()
                        }
                    }

                    createCurrencyAddingRecycler(currencySymbol)
                    currencyRecyclerModelLiveData.setValue(currencyRecyclerModel)
                }
            )
    }
}