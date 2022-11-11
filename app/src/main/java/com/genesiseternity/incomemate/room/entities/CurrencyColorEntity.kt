package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = CurrencyColorEntity.TABLE_NAME
)
data class CurrencyColorEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "current_color") val currentColor: Int
) {
    companion object {
        const val TABLE_NAME: String  = "CurrencyColor"
    }


}
