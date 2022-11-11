package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = PieChartEntity.TABLE_NAME
)
data class PieChartEntity (
    @PrimaryKey @ColumnInfo(name = "id_page") var idPage: Int,
    @ColumnInfo(name = "id") val id: Int,
) {
    companion object {
        const val TABLE_NAME: String  = "PieCharts"
    }
}