package com.genesiseternity.incomemate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyList(
    @SerializedName("USD") var usd: CurrencyBodyModel,
    @SerializedName("EUR") var eur: CurrencyBodyModel,
    @SerializedName("CNY") var cny: CurrencyBodyModel,
    @SerializedName("AUD") var aud: CurrencyBodyModel,
    @SerializedName("GBP") var gbp: CurrencyBodyModel,
    @SerializedName("CAD") var cad: CurrencyBodyModel,
    @SerializedName("CHF") var chf: CurrencyBodyModel,
    @SerializedName("JPY") var jpy: CurrencyBodyModel
)