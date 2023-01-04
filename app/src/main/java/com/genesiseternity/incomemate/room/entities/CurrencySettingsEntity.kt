package com.genesiseternity.incomemate.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = CurrencySettingsEntity.TABLE_NAME
)
data class CurrencySettingsEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "default_currency_type") val defaultCurrencyType: Int,
    @ColumnInfo(name = "default_language_type") val defaultLanguageType: Int,
    @ColumnInfo(name = "passcode") val passcode: Int,
    @ColumnInfo(name = "is_enabled_passcode") val isEnabledPasscode: Boolean
) {
    companion object {
        const val TABLE_NAME: String  = "CurrencySettings"
    }
}