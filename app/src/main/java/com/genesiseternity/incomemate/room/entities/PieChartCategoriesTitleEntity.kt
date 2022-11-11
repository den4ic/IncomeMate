package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = PieChartCategoriesTitleEntity.TABLE_NAME
)
data class PieChartCategoriesTitleEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title_category") val titleCategory: String,
    //@ColumnInfo(name = "amount_category") private var amountCategory: String,
    @ColumnInfo(name = "currency_type") val currencyType: Int,
    @ColumnInfo(name = "id_card_view_icon") val idCardViewIcon: Int,
    @ColumnInfo(name = "id_color_icon") val idColorIcon: Int
) {
    companion object {
        const val TABLE_NAME: String  = "PieChartCategoriesTitle"
    }
}