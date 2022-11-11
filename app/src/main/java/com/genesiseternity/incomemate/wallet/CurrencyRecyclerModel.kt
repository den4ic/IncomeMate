package com.genesiseternity.incomemate.wallet

data class CurrencyRecyclerModel(
    var idCurrency: Int,
    var titleCurrencyName: String,
    var amountCurrency: String,
    var currencyType: Int,
    var imgIconCurrency: Int,
    var selectedColorId: Int
)