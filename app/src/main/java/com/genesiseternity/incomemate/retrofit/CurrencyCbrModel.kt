package com.genesiseternity.incomemate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyCbrModel(
    @SerializedName("Date") val date: String,
    @SerializedName("Valute") val currencyList: CurrencyList
) {
    companion object {
        const val BASE_URL: String = "https://www.cbr-xml-daily.ru/"
    }
}