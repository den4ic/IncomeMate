package com.genesiseternity.incomemate.pieChart

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.retrofit.CurrencyBodyModel
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.room.PieChartCategoriesDao
import com.genesiseternity.incomemate.room.PieChartCategoriesTitleDao
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesTitleEntity
import com.genesiseternity.incomemate.utils.replaceToRegex
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

enum class StateCategoryType
{
    EXPENSES, INCOME
}

class PieChartFragmentViewModel @Inject constructor(
    private var application: Application,
    private var currencyFormat: dagger.Lazy<CurrencyFormat>,
    private var pieChartCategoriesDao: dagger.Lazy<PieChartCategoriesDao>,
    private var pieChartCategoriesTitleDao: dagger.Lazy<PieChartCategoriesTitleDao>,
    currencySettingsDao: dagger.Lazy<CurrencySettingsDao>,
    private val currencyCbrRepository: CurrencyCbrRepository
) : ViewModel(), IPieChartCategoryView
{
    var idPage: Int = 0
    private var defaultCurrencyType: Int = 0
    private val listCategoryExpenses: Array<String>
    private var colorGreen: Int = 0

    private val textAdd = "Добавить"
    private val textNewCategory = "Новая категория №"

    private val pieChartCategoryModelArrayList: ArrayList<PieChartCategoryModel> = ArrayList()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val pieChartCategoryModelListLiveData: MutableLiveData<ArrayList<PieChartCategoryModel>> = MutableLiveData()
    private val fillPieChartLiveData: MutableLiveData<PieChartModel> = MutableLiveData<PieChartModel>() // .apply { PieChartModel() }

    private lateinit var cbrCurrencyList: ArrayList<CurrencyBodyModel>
    private val currencySymbol: Array<String> by lazy { application.resources.getStringArray(R.array.list_currency_symbol) }

    private val _notifyItemAdapterLiveData: MutableLiveData<Int> = MutableLiveData()
    val notifyItemAdapterLiveData: MutableLiveData<Int> get() = _notifyItemAdapterLiveData

    init
    {
        listCategoryExpenses = application.resources.getStringArray(R.array.list_category_expenses_pie_chart)
        colorGreen = ContextCompat.getColor(application, R.color.green)

        compositeDisposable.add(currencySettingsDao.get().getDefaultCurrencyByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { defaultCurrencyType = it }, {} ))

        firstInitPieChart()
    }

    override fun onCleared()
    {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun getFillPieChartLiveData(): MutableLiveData<PieChartModel> = fillPieChartLiveData
    fun getPieChartCategoryModelListLiveData(): MutableLiveData<ArrayList<PieChartCategoryModel>> = pieChartCategoryModelListLiveData

    private fun firstInitPieChart()
    {
        val pieChartCategoriesTitleEntities: ArrayList<PieChartCategoriesTitleEntity> = ArrayList(listCategoryExpenses.size)

        val rnd: Random = Random()
        val colorRandList: IntArray = IntArray(size = listCategoryExpenses.size) { Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) }

        for (i in listCategoryExpenses.indices)
        {
            val pieChartCategoriesTitleEntity: PieChartCategoriesTitleEntity = PieChartCategoriesTitleEntity(
                i,
                listCategoryExpenses[i],
                defaultCurrencyType,
                i,
                colorRandList[i])

            pieChartCategoriesTitleEntities.add(pieChartCategoriesTitleEntity)
        }

        compositeDisposable.add(pieChartCategoriesTitleDao.get().insertPieChartCategoriesTitleData(*pieChartCategoriesTitleEntities.toTypedArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { initPieChart() }, { initPieChart() }
            ))
    }

    private fun initPieChart()
    {
        //pieChartCategoriesDao.get().getAllSortedPieChartCategoriesData()
        val disposablePieChartCategories: Disposable = pieChartCategoriesDao.get().getPieChartCategoryByIdPage(idPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            //.flatMapIterable { it }
            //.flatMapObservable { t -> Observable.fromIterable(t) }
            .subscribe(
                {
                    pieChartCategoriesEntities ->

                    val disposablePieChartCategoriesTitle: Disposable = pieChartCategoriesTitleDao.get().getAllPieChartCategoriesTitleData()
                        //.filter {
                        //    if (it.isNotEmpty()) {
                        //        return@filter true
                        //    } else {
                        //        firstInitPieChart()
                        //        return@filter false
                        //    }
                        //}
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        //.flatMapIterable { it }
                        //.flatMapObservable { t -> Observable.fromIterable(t) }
                        //.doOnSubscribe { it.request(Long.MAX_VALUE) }
                        .subscribe(
                            {
                                pieChartCategoriesTitleEntities ->

                                if (pieChartCategoriesEntities.size > 0)
                                {
                                    for (i in pieChartCategoriesTitleEntities.indices)
                                    {
                                        for (j in pieChartCategoriesEntities.indices)
                                        {
                                            if (pieChartCategoriesTitleEntities[i].id == pieChartCategoriesEntities[j].id)
                                            {
                                                pieChartCategoryModelArrayList.add(PieChartCategoryModel(
                                                    pieChartCategoriesEntities[j].id,
                                                    pieChartCategoriesTitleEntities[i].titleCategory,
                                                    pieChartCategoriesEntities[j].amountCategory,
                                                    pieChartCategoriesTitleEntities[i].idCardViewIcon,
                                                    pieChartCategoriesTitleEntities[i].idColorIcon,
                                                    pieChartCategoriesTitleEntities[i].currencyType))
                                                break
                                            }
                                            else if (pieChartCategoriesEntities[j].id != pieChartCategoriesTitleEntities[i].id &&
                                                j == pieChartCategoriesEntities.size-1)
                                            {
                                                pieChartCategoryModelArrayList.add(PieChartCategoryModel(
                                                    pieChartCategoriesTitleEntities[i].id,
                                                    pieChartCategoriesTitleEntities[i].titleCategory,
                                                    "0",
                                                    pieChartCategoriesTitleEntities[i].idCardViewIcon,
                                                    pieChartCategoriesTitleEntities[i].idColorIcon,
                                                    pieChartCategoriesTitleEntities[i].currencyType))
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    for (i in pieChartCategoriesTitleEntities.indices)
                                    {
                                        pieChartCategoryModelArrayList.add(PieChartCategoryModel(
                                            pieChartCategoriesTitleEntities[i].id,
                                            pieChartCategoriesTitleEntities[i].titleCategory,
                                            "0",
                                            pieChartCategoriesTitleEntities[i].idCardViewIcon,
                                            pieChartCategoriesTitleEntities[i].idColorIcon,
                                            pieChartCategoriesTitleEntities[i].currencyType))
                                    }
                                }
                                pieChartCategoryModelArrayList.add(PieChartCategoryModel(-1, textAdd, "", 0, 0, defaultCurrencyType))
                                pieChartCategoryModelListLiveData.value = pieChartCategoryModelArrayList

                                updateAllAmountCurrency()
                            },
                            {
                            }
                        )
                    compositeDisposable.add(disposablePieChartCategoriesTitle)
                },
                {
                }
            )

        compositeDisposable.add(disposablePieChartCategories)
    }

    private fun saveNewCategory(index: Int)
    {
        var numAccount: Int = index
        val titleCategoryName: String = textNewCategory + ++numAccount

        val pieChartCategoriesTitleEntity: PieChartCategoriesTitleEntity = PieChartCategoriesTitleEntity(
            index,
            titleCategoryName,
            defaultCurrencyType,
            0,
            colorGreen)

        compositeDisposable.add(pieChartCategoriesTitleDao.get().insertPieChartCategoriesTitleData(pieChartCategoriesTitleEntity)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    goToSelectedActivity(index)
                },
                {
                    goToSelectedActivity(index)
                }
            ))
    }

    override fun onItemClick(pos: Int)
    {
        val insertIndex: Int = pieChartCategoryModelArrayList.size-1

        if (pos != insertIndex)
        {
            goToSelectedActivity(pos)
        }
        else
        {
            var checkRemovedCurrencyRecycler: Boolean = false
            var missingIndex: Int = 0

            for (i in pieChartCategoryModelArrayList.indices)
            {
                if (i < pieChartCategoryModelArrayList[i].idCategory)
                {
                    checkRemovedCurrencyRecycler = true
                    missingIndex = i
                    break
                }
            }

            if (checkRemovedCurrencyRecycler)
            {
                pieChartCategoryModelArrayList.add(missingIndex, PieChartCategoryModel(
                    missingIndex,
                    textNewCategory + missingIndex,
                    "0",
                    0,
                    0,
                    defaultCurrencyType)
                )

                saveNewCategory(missingIndex)
            }
            else
            {
                var numAccount: Int = insertIndex
                pieChartCategoryModelArrayList.add(insertIndex, PieChartCategoryModel(
                    pos,
                    textNewCategory + ++numAccount,
                    "0",
                    0,
                    0,
                    defaultCurrencyType))

                saveNewCategory(insertIndex)
            }
        }
    }


    private fun goToSelectedActivity(idPos: Int)
    {
        _notifyItemAdapterLiveData.value = idPos

        val intent: Intent = Intent(application.baseContext, CategoryActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("idCategory", pieChartCategoryModelArrayList[idPos].idCategory)
        intent.putExtra("idPage", idPage)
        intent.putExtra("titleCategoryName", pieChartCategoryModelArrayList[idPos].titleCategoryName)
        intent.putExtra("amountCategory", pieChartCategoryModelArrayList[idPos].amountCategory)
        intent.putExtra("imageCategory", pieChartCategoryModelArrayList[idPos].imageCategory)
        intent.putExtra("selectedColorId", pieChartCategoryModelArrayList[idPos].selectedColorId)
        intent.putExtra("defaultCurrencyType", pieChartCategoryModelArrayList[idPos].currencyType)
        application.startActivity(intent)
    }

    private fun fillPieChart(): PieChartModel
    {
        val sizeCategoryArrayList: Int = pieChartCategoryModelArrayList.size-1
        val data: FloatArray = FloatArray(sizeCategoryArrayList)
        val color: IntArray = IntArray(size = sizeCategoryArrayList)

        var amountCurrency: Float = 0f

        for (i in 0 until sizeCategoryArrayList)
        {
            //String tempAmountCurrency = recyclerCurrencies.get(i).getAmountCurrency().replaceAll("[^0-9]", "")
            val tempAmountCurrency: String = pieChartCategoryModelArrayList[i].amountCategory.replaceToRegex()
            val tempSelectedColorId: Int = pieChartCategoryModelArrayList[i].selectedColorId

            if (tempAmountCurrency.isNotEmpty())
            {
                data[i] = (if (tempAmountCurrency.startsWith("-")) "0" else tempAmountCurrency).toFloat()

                if (pieChartCategoryModelArrayList[i].currencyType != 0)
                {
                    for (j in cbrCurrencyList.indices)
                    {
                        if (pieChartCategoryModelArrayList[i].currencyType == j)
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

            color[i] = tempSelectedColorId
            //color = intArrayOf(tempSelectedColorId)
        }

        amountCurrency /= cbrCurrencyList[defaultCurrencyType].value / cbrCurrencyList[defaultCurrencyType].nominal

        return PieChartModel(
            if (amountCurrency != 0.0f) currencyFormat.get().setStringTextFormatted(amountCurrency.toString()) + " " +  currencySymbol[defaultCurrencyType] else "0",
            data,
            color
        )
    }

    private fun updateAllAmountCurrency()
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

                    fillPieChartLiveData.value = fillPieChart()
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

                    fillPieChartLiveData.value = fillPieChart()
                }
            ))
    }

}