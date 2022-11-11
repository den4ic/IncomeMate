package com.genesiseternity.incomemate.room

import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity

interface CurrencyDetailsRepository {
    fun getAllCurrencyData(): List<CurrencyDetailsEntity>

    fun getAllSortedCurrencyData(): List<CurrencyDetailsEntity>

    fun insertCurrencyData(vararg currencyDetailsEntities: CurrencyDetailsEntity)

    fun updateCurrencyData(currencyDetailsEntities: CurrencyDetailsEntity)

    fun deleteCurrencyData(currencyDetailsEntities: ArrayList<CurrencyDetailsEntity>)

    fun deleteAllCurrencyData()
}