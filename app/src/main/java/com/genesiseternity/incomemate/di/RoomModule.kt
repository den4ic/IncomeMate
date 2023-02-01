package com.genesiseternity.incomemate.di

import android.app.Application
import androidx.room.Room
import com.genesiseternity.incomemate.room.*
import com.genesiseternity.incomemate.utils.LanguageConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule(val application: Application)
{
    @Singleton
    @Provides
    fun provideRoomDatabase() : AppDatabase
    {
        return Room.databaseBuilder(application, AppDatabase::class.java, "IncomeDatabase")
            //.fallbackToDestructiveMigration()
            //.allowMainThreadQueries()
            .build()
    }

    // TODO("move to separate module with application")
    @Singleton
    @Provides
    fun provideLanguageConfig() : LanguageConfig = LanguageConfig(application.resources)

    /*
    @Singleton
    @Provides
    fun getRoomDbInstance(): AppDatabase
    {
        return AppDatabase.getAppDatabaseInstance(provideAppContext())
    }

    @Singleton
    @Provides
    fun provideAppContext(): Context
    {
        return application.applicationContext
    }
    */

    @Singleton
    @Provides
    fun provideCurrencyDetailsDao(appDatabase: AppDatabase): CurrencyDetailsDao
    {
        return appDatabase.getCurrencyDetailsDao()
    }

    @Singleton
    @Provides
    fun provideCurrencyColorDao(appDatabase: AppDatabase): CurrencyColorDao
    {
        return appDatabase.getCurrencyColorDao()
    }

    @Singleton
    @Provides
    fun provideCurrencySettingsDao(appDatabase: AppDatabase): CurrencySettingsDao
    {
        return appDatabase.getCurrencySettingsDao()
    }

    @Singleton
    @Provides
    fun providePieChartCategoriesDao(appDatabase: AppDatabase): PieChartCategoriesDao
    {
        return appDatabase.getPieChartCategoriesDao()
    }

    @Singleton
    @Provides
    fun providePieChartCategoriesTitleDao(appDatabase: AppDatabase): PieChartCategoriesTitleDao
    {
        return appDatabase.getPieChartCategoriesTitleDao()
    }

    @Singleton
    @Provides
    fun providePieChartDao(appDatabase: AppDatabase): PieChartDao
    {
        return appDatabase.getPieChartDao()
    }

}