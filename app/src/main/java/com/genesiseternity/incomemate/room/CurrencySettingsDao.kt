package com.genesiseternity.incomemate.room

import androidx.room.*
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


    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET default_currency_type = :defaultCurrency WHERE id = :id")
    fun updateDefaultCurrency(id: Int, defaultCurrency: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET default_language_type = :defaultLanguage WHERE id = :id")
    fun updateDefaultLanguage(id: Int, defaultLanguage: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET passcode = :passcode WHERE id = :id")
    fun updatePasscode(id: Int, passcode: Int): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET is_enabled_passcode = :isEnabledPasscode WHERE id = :id")
    fun updateEnabledPasscode(id: Int, isEnabledPasscode: Boolean): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET is_enabled_night_mode = :isEnabledNightMode WHERE id = :id")
    fun updateEnabledNightMode(isEnabledNightMode: Boolean, id: Int = 0): Completable

    @Query("UPDATE " + CurrencySettingsEntity.TABLE_NAME + " SET default_id_currency_account = :defaultIdCurrencyAccount WHERE id = :id")
    fun updateDefaultIdCurrencyAccount(defaultIdCurrencyAccount: Int, id: Int = 0): Completable


    @Query("SELECT default_currency_type FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getDefaultCurrencyByIdPage(): Single<Int>

    @Query("SELECT default_language_type FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getDefaultLanguageByIdPage(): Single<Int>

    @Query("SELECT passcode FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getPasscodeByIdPage(): Single<Int>

    @Query("SELECT is_enabled_passcode FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getEnabledPasscodeByIdPage(): Single<Boolean>

    @Query("SELECT is_enabled_night_mode FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getEnabledNightModeByIdPage(): Single<Boolean>

    @Query("SELECT default_id_currency_account FROM " + CurrencySettingsEntity.TABLE_NAME)
    fun getDefaultIdCurrencyAccountByIdPage(): Single<Int>
}