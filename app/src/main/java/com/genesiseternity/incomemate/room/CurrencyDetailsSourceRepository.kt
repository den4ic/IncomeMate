package com.genesiseternity.incomemate.room

import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CurrencyDetailsSourceRepository @Inject public constructor(private val currencyDetailsDao: CurrencyDetailsDao) {

    fun insertCurrencyData(vararg currencyDetailsEntities: CurrencyDetailsEntity) : Completable
    {
        //return currencyDetailsDao.insertCurrencyData(currencyDetailsEntities)
        TODO()
    }

    fun getAllSortedCurrencyData(): Single<List<CurrencyDetailsEntity>>
    {
        return currencyDetailsDao.getAllSortedCurrencyData()
    }

}