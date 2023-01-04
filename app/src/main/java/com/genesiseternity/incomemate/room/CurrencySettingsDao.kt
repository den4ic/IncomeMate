package com.genesiseternity.incomemate.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.genesiseternity.incomemate.room.entities.CurrencySettingsEntity
import com.genesiseternity.incomemate.room.entities.PieChartCategoriesEntity
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


    //@Query("INSERT INTO CurrencySettings (default_currency_type) VALUES (null,:defaultCurrency,null,null)")
    //fun insertDefaultCurrency(defaultCurrency: Int): Completable


    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET default_currency_type = :defaultCurrency WHERE id = :id")
    fun updateDefaultCurrency(id: Int, defaultCurrency: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET default_language_type = :defaultLanguage WHERE id = :id")
    fun updateDefaultLanguage(id: Int, defaultLanguage: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET passcode = :passcode WHERE id = :id")
    fun updatePasscode(id: Int, passcode: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET is_enabled_passcode = :isEnabledPasscode WHERE id = :id")
    fun updateEnabledPasscode(id: Int, isEnabledPasscode: Boolean): Completable


    @Query("SELECT default_currency_type FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getDefaultCurrencyByIdPage(): Single<Int>

    @Query("SELECT default_language_type FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getDefaultLanguageByIdPage(): Single<Int>

    @Query("SELECT passcode FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getPasscodeByIdPage(): Single<Int>

    @Query("SELECT is_enabled_passcode FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getEnabledPasscodeByIdPage(): Single<Boolean>
}