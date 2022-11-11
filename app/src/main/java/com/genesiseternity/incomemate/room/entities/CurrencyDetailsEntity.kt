package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = CurrencyDetailsEntity.TABLE_NAME
)
data class CurrencyDetailsEntity (
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "title_category") var titleCurrency: String,
    @ColumnInfo(name = "amount_currency") var amountCurrency: String,
    @ColumnInfo(name = "currency_type") val currencyType: Int,
    @ColumnInfo(name = "id_card_view_icon") val idCardViewIcon: Int,
    @ColumnInfo(name = "id_color_icon") val idColorIcon: Int
) {
    companion object {
        const val TABLE_NAME: String  = "CurrencyDetails"
    }



}


