package com.genesiseternity.incomemate.retrofit

import com.google.gson.annotations.SerializedName

data class CurrencyBodyModel (
    @SerializedName("Name") var name: String,
    @SerializedName("Value") var value: String
)