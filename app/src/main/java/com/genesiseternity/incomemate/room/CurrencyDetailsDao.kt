package com.genesiseternity.incomemate.room

import androidx.room.*
import com.genesiseternity.incomemate.room.entities.CurrencyDetailsEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrencyDetailsDao {

    @Query("SELECT * FROM " + CurrencyDetailsEntity.TABLE_NAME)
    fun getAllCurrencyData(): Single<List<CurrencyDetailsEntity>>

    @Query("SELECT * FROM " + CurrencyDetailsEntity.TABLE_NAME + " ORDER BY id ASC")
    fun getAllSortedCurrencyData(): Single<List<CurrencyDetailsEntity>>

    @Insert
    fun insertCurrencyData(vararg currencyDetailsEntities: CurrencyDetailsEntity): Completable

    @Update
    fun updateCurrencyData(currencyDetailsEntities: CurrencyDetailsEntity): Completable

    @Delete
    fun deleteCurrencyData(currencyDetailsEntities: ArrayList<CurrencyDetailsEntity>): Completable

    @Query("DELETE FROM " + CurrencyDetailsEntity.TABLE_NAME + " WHERE id = :id")
    fun deleteCurrentCurrencyData(id: Int): Completable

    @Query("DELETE FROM " + CurrencyDetailsEntity.TABLE_NAME)
    fun deleteAllCurrencyData(): Completable


    @Query("SELECT * FROM " + CurrencyDetailsEntity.TABLE_NAME + " ORDER BY id ASC")
    fun getAll(): Flowable<List<CurrencyDetailsEntity>>


    @Query("SELECT amount_currency FROM " + CurrencyDetailsEntity.TABLE_NAME + " WHERE id = :id")
    fun getAmountCurrencyById(id: Int): Single<List<String>>

    @Query("UPDATE " + CurrencyDetailsEntity.TABLE_NAME + " SET amount_currency = :amountCurrency WHERE id = :id")
    fun updateCurrentAccount(id: Int, amountCurrency: String): Completable

}