package com.genesiseternity.incomemate.pieChart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.CurrencyConverter
import com.genesiseternity.incomemate.R
import com.genesiseternity.incomemate.room.CurrencyDetailsDao
import com.genesiseternity.incomemate.room.CurrencySettingsDao
import com.genesiseternity.incomemate.wallet.CurrencyAccountRecyclerModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class PieChartHeadViewModel @Inject constructor(
    private val application: Application,
    private val currencySettingsDao: dagger.Lazy<CurrencySettingsDao>,
    private val currencyDetailsDao: dagger.Lazy<CurrencyDetailsDao>,
    private var currencyConverter: dagger.Lazy<CurrencyConverter>
) : ViewModel()
{
    val getCounterUpdatePage: Int
    val getCurrentDate: Date

    companion object {
        private var counterUpdatePage: Int = 0
        private var currentDate: Date = Date()
        private var lastDate: Date = Date()
        private var isFirstInitPage: Boolean = true
    }

    private var currencyAccountRecyclerModel: ArrayList<CurrencyAccountRecyclerModel> = ArrayList()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var dateFormat: DateFormat
    private val uiPatternDate: String = "EEE, dd MMMM yyyy"
    private val MAX_COUNT_UPDATE_PAGE: Int = 2

    private var defaultCurrencyType: Int = 0
    private val currencySymbol: Array<String> by lazy { application.resources.getStringArray(R.array.list_currency_symbol) }

    private val listCurrencyAccountRecyclerModel: MutableLiveData<MutableList<CurrencyAccountRecyclerModel>> = MutableLiveData()

    init
    {
        counterUpdatePage++

        if (counterUpdatePage >= MAX_COUNT_UPDATE_PAGE)
        {
            lastDate = Date()
            counterUpdatePage = 0
        }

        currentDate = if (isFirstInitPage) Date() else lastDate

        getCounterUpdatePage = counterUpdatePage
        getCurrentDate = currentDate

        getDefaultCurrencyType()
        initAllMonetaryAccount()
    }

    override fun onCleared()
    {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun setCurrentDate(): String = SimpleDateFormat(uiPatternDate, Locale.getDefault()).format(currentDate)

    fun daysBetween(d1: Date, d2: Date): Int = ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt() // day

    fun initDateFormat(currentDay: Int, saveCurrentDay: Int): String
    {
        val counterDay: Int = currentDay - saveCurrentDay
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, counterDay)
        dateFormat = SimpleDateFormat(uiPatternDate, Locale.getDefault()) // EEE, dd MMMM yyyy
        val dt: String = dateFormat.format(calendar.time)

        isFirstInitPage = false
        lastDate = calendar.time
        return dt
    }

    fun getListCurrencyRecyclerModel(): MutableLiveData<MutableList<CurrencyAccountRecyclerModel>> = listCurrencyAccountRecyclerModel

    private fun getDefaultCurrencyType()
    {
        compositeDisposable.add(currencySettingsDao.get().getDefaultCurrencyByIdPage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { defaultCurrencyType = it }, {} ))
    }

    private fun initAllMonetaryAccount()
    {
        val listMonetaryAccounts: String = application.resources.getString(R.string.list_monetary_account)

        compositeDisposable.add(currencyDetailsDao.get().getAllSortedCurrencyData()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { t -> Observable.fromIterable(t) }
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
                        currencyDetailsEntities.idColorIcon)
                    )

                },
                {
                },
                {
                    currencyConverter.get().defaultCurrencyType = defaultCurrencyType
                    currencyConverter.get().currencySymbol = currencySymbol
                    currencyConverter.get().updateAllCurrencyAmount(::setTotalCashAccount, currencyAccountRecyclerModel)

                    currencyAccountRecyclerModel.add(CurrencyAccountRecyclerModel(
                        currencyAccountRecyclerModel.lastIndex+1,
                        listMonetaryAccounts,
                        "0",
                        0,
                        0,
                        0))
                }
            ))
    }

    private fun setTotalCashAccount(res: String)
    {
        currencyAccountRecyclerModel[currencyAccountRecyclerModel.lastIndex].amountCurrency = res
        listCurrencyAccountRecyclerModel.value = currencyAccountRecyclerModel
    }
}