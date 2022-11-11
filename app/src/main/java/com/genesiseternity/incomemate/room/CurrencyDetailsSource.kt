package com.genesiseternity.incomemate.room

import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity

class CurrencyDetailsSource(private val currencyDetailsDao: CurrencyDetailsDao) : CurrencyDetailsRepository {

    override fun getAllCurrencyData(): List<CurrencyDetailsEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllSortedCurrencyData(): List<CurrencyDetailsEntity> {
        TODO("Not yet implemented")
    }

    override fun insertCurrencyData(vararg currencyDetailsEntities: CurrencyDetailsEntity) {
        //currencyDetailsDao.insertCurrencyData(currencyDetailsEntities)
        TODO("Not yet implemented")
    }

    override fun updateCurrencyData(currencyDetailsEntities: CurrencyDetailsEntity) {
        currencyDetailsDao.updateCurrencyData(currencyDetailsEntities)
    }

    override fun deleteCurrencyData(currencyDetailsEntities: ArrayList<CurrencyDetailsEntity>) {
        currencyDetailsDao.deleteCurrencyData(currencyDetailsEntities)
    }

    override fun deleteAllCurrencyData() {
        currencyDetailsDao.deleteAllCurrencyData()
    }

}