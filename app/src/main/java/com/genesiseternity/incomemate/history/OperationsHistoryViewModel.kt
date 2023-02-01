package com.genesiseternity.incomemate.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.genesiseternity.incomemate.utils.replaceToRegex
import com.genesiseternity.incomemate.wallet.IRecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class OperationsHistoryViewModel @Inject constructor(private val application: Application) : ViewModel(), IRecyclerView
{
    @Inject lateinit var currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>
    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>
    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>

    private val _historyRecyclerModelLiveData: MutableLiveData<List<HistoryRecyclerModel>> = MutableLiveData()
    val historyRecyclerModelLiveData: MutableLiveData<List<HistoryRecyclerModel>> get() = _historyRecyclerModelLiveData

    private val historyRecyclerModels: ArrayList<HistoryRecyclerModel> = ArrayList()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    /*
   private fun hasData(data: FloatArray): Boolean
   {
       for (i in data.indices-1)
       {
           if (data[i] != data[(i + 1)])
           {
               return true
           }
       }
       return false
   }
     */

    fun initHistoryList()
    {
        var listCurrency: List<CurrencyDetailsEntity> = emptyList()

        compositeDisposable.add(currencyDetailsDao.get().getAllCurrencyData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { listCurrency = it }, { it.printStackTrace() } ))


        val map: HashMap<List<PieChartCategoriesEntity>, List<PieChartCategoriesTitleEntity>> = HashMap()

        compositeDisposable.add(Observable.zip(
            Observable.fromCallable { pieChartCategoriesDao.get().getSortedPieChartCategoriesData() }.subscribeOn(Schedulers.io()),
            Observable.fromCallable { pieChartCategoriesTitleDao.get().getAllCategoriesTitleData() }.subscribeOn(Schedulers.io())
        ) { pieChartCategoriesDao, pieChartCategoriesTitleDao -> map[pieChartCategoriesDao] = pieChartCategoriesTitleDao
        }
        //.subscribeOn(Schedulers.io())
        //.flatMapObservable { t -> Observable.fromIterable(t) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                //val (s, t, w) = it

                var amountCash: Float = 0.0f
                var historyRecyclerModel: HistoryRecyclerModel = HistoryRecyclerModel()
                var pieChartCategoriesEntitiesLength: Int

                for ((pieChartCategoriesEntities, pieChartCategoriesTitleEntities) in map)
                {
                    pieChartCategoriesEntitiesLength = pieChartCategoriesEntities.size


                    for (i in 0 until pieChartCategoriesEntitiesLength)
                    {
                        val isUniquePage: Boolean = i == 0 || pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i - 1].idPage

                        if (isUniquePage)
                        {
                            historyRecyclerModel = HistoryRecyclerModel()
                            historyRecyclerModel.date = pieChartCategoriesEntities[i].idPage.toString()

                            amountCash = 0.0f
                        }

                        val tempAmountCurrency: String = pieChartCategoriesEntities[i].amountCategory.replaceToRegex()
                        if (tempAmountCurrency.isNotEmpty())
                        {
                            amountCash += tempAmountCurrency.toFloat()
                        }

                        if ((i < pieChartCategoriesEntitiesLength - 1 && pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i + 1].idPage)
                            || i == pieChartCategoriesEntitiesLength - 1)
                        {
                            historyRecyclerModel.amountCash = amountCash.toString()
                        }

                        if (isUniquePage)
                        {
                            historyRecyclerModels.add(historyRecyclerModel)
                        }


                        for (j in pieChartCategoriesTitleEntities.indices)
                        {
                            if (pieChartCategoriesTitleEntities[j].id == pieChartCategoriesEntities[i].id)
                            {
                                historyRecyclerModels.add(HistoryRecyclerModel(
                                    "",
                                    pieChartCategoriesEntities[i].amountCategory,
                                    pieChartCategoriesTitleEntities[j].titleCategory,
                                    listCurrency[pieChartCategoriesEntities[i].idCurrencyAccount].titleCurrency,
                                    pieChartCategoriesTitleEntities[j].idCardViewIcon,
                                    listCurrency[pieChartCategoriesEntities[i].idCurrencyAccount].idCardViewIcon,
                                    pieChartCategoriesTitleEntities[j].idColorIcon,
                                    listCurrency[pieChartCategoriesEntities[i].idCurrencyAccount].idColorIcon
                                ))

                            }
                        }
                    }
                }
            },
            {

            },
            {
                _historyRecyclerModelLiveData.setValue(historyRecyclerModels)
            }
        ))
    }

    override fun onItemClick(pos: Int) {
        Log.d("OperationsHistory", "OperationsHistoryFragment - " + pos)
    }
}