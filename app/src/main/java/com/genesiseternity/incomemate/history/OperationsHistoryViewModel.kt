package com.genesiseternity.incomemate.history

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.genesiseternity.incomemate.wallet.IRecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class OperationsHistoryViewModel @Inject constructor(private val application: Application) : ViewModel(), IRecyclerView {

    @Inject lateinit var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>
    @Inject lateinit var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>

    private var historyRecyclerModelLiveData: MutableLiveData<List<HistoryRecyclerModel>>
    fun getCurrencyRecyclerModelLiveData(): MutableLiveData<List<HistoryRecyclerModel>> = historyRecyclerModelLiveData

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    fun getCompositeDisposable(): CompositeDisposable = compositeDisposable

    private val historyRecyclerModels: ArrayList<HistoryRecyclerModel> = ArrayList()

    init {
        historyRecyclerModelLiveData = MutableLiveData()
    }

    override fun onCleared() {
        getCompositeDisposable().dispose()
        super.onCleared()
    }

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

    /*
    fun initHistoryList()
    {
        //pieChartCategoriesDao.get().getAllPieChartCategoriesData()
        pieChartCategoriesDao.get().getSortedDescPieChartCategoriesData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    pieChartCategoriesEntities ->

                    pieChartCategoriesTitleDao.get().getAllPieChartCategoriesTitleData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                pieChartCategoriesTitleEntities ->

                                var amountCash: Float = 0.0f
                                var historyRecyclerModel: HistoryRecyclerModel = HistoryRecyclerModel()
                                val pieChartCategoriesEntitiesLength: Int = pieChartCategoriesEntities.size

                                for (i in 0 until pieChartCategoriesEntitiesLength)
                                {
                                    val isUniquePage: Boolean = i == 0 || pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i - 1].idPage

                                    if (isUniquePage)
                                    {
                                        historyRecyclerModel = HistoryRecyclerModel()
                                        historyRecyclerModel.date = pieChartCategoriesEntities[i].idPage.toString()

                                        amountCash = 0.0f
                                    }

                                    val tempAmountCurrency: String = pieChartCategoriesEntities[i].amountCategory.replace("[^\\d.-]".toRegex(), "")
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
                                        if (pieChartCategoriesTitleEntities.get(j).id == pieChartCategoriesEntities.get(i).id)
                                        {
                                            historyRecyclerModels.add(HistoryRecyclerModel(
                                                "",
                                                pieChartCategoriesEntities[i].amountCategory,
                                                pieChartCategoriesTitleEntities[j].titleCategory,
                                                pieChartCategoriesEntities[i].idPage.toString(),
                                                pieChartCategoriesTitleEntities[j].idCardViewIcon,
                                                0,
                                                pieChartCategoriesTitleEntities[j].idColorIcon,
                                                0
                                            ))

                                        }
                                    }

                                }

                                //Log.d("OperationsHistory", "hashSet - " + hashSet.iterator().next())

                                historyRecyclerModelLiveData.setValue(historyRecyclerModels)
                            },
                            {

                            }
                        )
                },
                {

                }
            )

    }
     */


    val zippedStrings: List<String> = ArrayList()

    fun initHistoryList()
    {

        /*
        Observable.zip(
            Observable.fromIterable(arrayListOf( "Simple", "Moderate", "Complex" )),
            Observable.fromIterable(arrayListOf( "Solutions", "Success", "Hierarchy" )),

            (str1, str2) -> str1 + " " + str2).subscribe(zippedStrings::add)
            //pieChartCategoriesDao.get().getSortedPieChartCategoriesData(),
            //pieChartCategoriesTitleDao.get().getAllCategoriesTitleData(),


            //Observable.fromSingle { pieChartCategoriesDao.get().getSortedDescPieChartCategoriesData() }
            //Observable.fromSingle { pieChartCategoriesTitleDao.get().getAllPieChartCategoriesTitleData() }
        )

         */
            //.subscribeOn(Schedulers.io())
            //.observeOn(AndroidSchedulers.mainThread())




        val map: HashMap<List<PieChartCategoriesEntity>, List<PieChartCategoriesTitleEntity>> = HashMap()

        compositeDisposable.add(Observable.zip(
            Observable.fromCallable { pieChartCategoriesDao.get().getSortedPieChartCategoriesData() },
            Observable.fromCallable { pieChartCategoriesTitleDao.get().getAllCategoriesTitleData() }
        ) { list1, list2 -> map[list1] = list2 // list1, list2 -> list1 + list2
        }
        .subscribeOn(Schedulers.io())
        //.flatMapObservable { t -> Observable.fromIterable(t) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                //val (s, t, w) = it

                var amountCash: Float = 0.0f
                var historyRecyclerModel: HistoryRecyclerModel = HistoryRecyclerModel()
                var pieChartCategoriesEntitiesLength: Int

                for ((pieChartCategoriesEntities, pieChartCategoriesTitleEntities) in map) {
                    //Log.d("OperationsHistory", "111 - $pieChartCategoriesEntities")
                    //Log.d("OperationsHistory", "222 - $pieChartCategoriesTitleEntities")

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

                        val tempAmountCurrency: String = pieChartCategoriesEntities[i].amountCategory.replace("[^\\d.-]".toRegex(), "")
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
                                    pieChartCategoriesEntities[i].idPage.toString(),
                                    pieChartCategoriesTitleEntities[j].idCardViewIcon,
                                    0,
                                    pieChartCategoriesTitleEntities[j].idColorIcon,
                                    0
                                ))

                            }
                        }

                    }

                }


                //Log.d("OperationsHistory", "1 - " + it)
            },
            {

            },
            {
                historyRecyclerModelLiveData.setValue(historyRecyclerModels)
            }
        ))


        /*

        //pieChartCategoriesDao.get().getAllPieChartCategoriesData()
        pieChartCategoriesDao.get().getSortedDescPieChartCategoriesData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    pieChartCategoriesEntities ->

                    pieChartCategoriesTitleDao.get().getAllPieChartCategoriesTitleData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                pieChartCategoriesTitleEntities ->

                                var amountCash: Float = 0.0f
                                var historyRecyclerModel: HistoryRecyclerModel = HistoryRecyclerModel()
                                val pieChartCategoriesEntitiesLength: Int = pieChartCategoriesEntities.size

                                for (i in 0 until pieChartCategoriesEntitiesLength)
                                {
                                    val isUniquePage: Boolean = i == 0 || pieChartCategoriesEntities[i].idPage != pieChartCategoriesEntities[i - 1].idPage

                                    if (isUniquePage)
                                    {
                                        historyRecyclerModel = HistoryRecyclerModel()
                                        historyRecyclerModel.date = pieChartCategoriesEntities[i].idPage.toString()

                                        amountCash = 0.0f
                                    }

                                    val tempAmountCurrency: String = pieChartCategoriesEntities[i].amountCategory.replace("[^\\d.-]".toRegex(), "")
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
                                        if (pieChartCategoriesTitleEntities.get(j).id == pieChartCategoriesEntities.get(i).id)
                                        {
                                            historyRecyclerModels.add(HistoryRecyclerModel(
                                                "",
                                                pieChartCategoriesEntities[i].amountCategory,
                                                pieChartCategoriesTitleEntities[j].titleCategory,
                                                pieChartCategoriesEntities[i].idPage.toString(),
                                                pieChartCategoriesTitleEntities[j].idCardViewIcon,
                                                0,
                                                pieChartCategoriesTitleEntities[j].idColorIcon,
                                                0
                                            ))

                                        }
                                    }

                                }

                                //Log.d("OperationsHistory", "hashSet - " + hashSet.iterator().next())

                                historyRecyclerModelLiveData.setValue(historyRecyclerModels)
                            },
                            {

                            }
                        )
                },
                {

                }
            )


         */
    }


    override fun onItemClick(pos: Int) {
        Log.d("OperationsHistory", "OperationsHistoryFragment - " + pos)
    }
}