package com.genesiseternity.incomemate.retrofit

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CurrencyCbrRepository @Inject constructor(private val currencyConverterCbrApi: CurrencyConverterCbrApi) {
    fun getLastCurrencyDate(): Single<CurrencyCbrModel> = currencyConverterCbrApi.getLastCurrencyDate()
}