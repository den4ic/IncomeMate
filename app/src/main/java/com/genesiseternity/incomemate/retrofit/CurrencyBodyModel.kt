package com.genesiseternity.incomemate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyBodyModel (
    @SerializedName("Name") var name: String,
    @SerializedName("Value") var value: Float,
    @SerializedName("Nominal") var nominal: Int
)