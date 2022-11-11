package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = PieChartCategoriesEntity.TABLE_NAME,
    primaryKeys = ["id", "id_page"],
    foreignKeys = [
        ForeignKey(entity = PieChartEntity::class,
        parentColumns = ["id_page"],
        childColumns = ["id_page"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class PieChartCategoriesEntity (
    var id: Int,
    @ColumnInfo(name = "id_page") var idPage: Int,
    @ColumnInfo(name = "amount_category") var amountCategory: String,
) {
    companion object {
        const val TABLE_NAME: String  = "PieChartCategories"
    }
}
