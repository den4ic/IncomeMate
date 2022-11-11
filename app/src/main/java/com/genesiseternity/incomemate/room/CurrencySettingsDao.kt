package com.genesiseternity.incomemate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrencySettingsDao {

    @Query("SELECT * FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getAllCurrencySettingsData(): Single<List<CurrencySettingsEntity>>

    @Insert
    fun insertCurrencySettingsData(currencySettingsEntities: CurrencySettingsEntity): Completable

    @Update
    fun updateCurrencySettingsData(currencySettingsEntities: CurrencySettingsEntity): Completable

    @Query("DELETE FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun deleteAllCurrencySettingsData(): Completable
}