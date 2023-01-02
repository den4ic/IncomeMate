package com.genesiseternity.incomemate.pieChart

data class PieChartCategoryModel (
    val idCategory: Int,
    val titleCategoryName: String,
    val amountCategory: String,
    val imageCategory: Int,
    val selectedColorId: Int,
    val currencyType: Int
)