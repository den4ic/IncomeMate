package com.genesiseternity.incomemate.history

data class HistoryRecyclerModel (
    var date: String = "",
    var amountCash: String = "",
    val titleCategoryName: String = "",
    val titleWalletName: String = "",
    val iconCategory: Int = 0,
    val iconWallet: Int = 0,
    val colorIdCategory: Int = 0,
    val colorIdWallet: Int = 0
)