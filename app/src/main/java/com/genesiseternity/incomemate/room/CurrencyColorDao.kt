package com.genesiseternity.incomemate.room

import androidx.room.*
import com.genesiseternity.incomemate.room.entities.CurrencyColorEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrencyColorDao {
    @Query("SELECT * FROM " + CurrencyColorEntity.TABLE_NAME)
    fun getAllCurrencyColorData(): Single<List<CurrencyColorEntity>>

    @Query("SELECT * FROM " + CurrencyColorEntity.TABLE_NAME + " ORDER BY id ASC")
    fun getAllSortedCurrencyColorData(): Single<List<CurrencyColorEntity>>

    @Insert
    fun insertCurrencyColorData(currencyColorEntities: CurrencyColorEntity): Completable

    @Update
    fun updateCurrencyColorData(currencyColorEntities: CurrencyColorEntity): Completable

    @Delete
    fun deleteCurrencyColorData(currencyColorEntity: CurrencyColorEntity): Completable

    @Query("DELETE FROM " + CurrencyColorEntity.TABLE_NAME)
    fun deleteAllCurrencyColorData(): Completable
}