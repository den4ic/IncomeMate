package com.genesiseternity.incomemate.di

import android.app.Application
import com.genesiseternity.incomemate.CurrencyConverter
import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.retrofit.CurrencyCbrRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [CurrencyFormatModule::class])
class AppModule {
    /*
    val mApplication: Application

    public constructor(application: Application) {
        mApplication = application
    }

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return mApplication
    }

     */

    //@Singleton
    @Provides
    fun provideCurrencyConverter(currencyCbrRepository: CurrencyCbrRepository, currencyFormat: CurrencyFormat) : CurrencyConverter
    {
        return CurrencyConverter(currencyCbrRepository, currencyFormat)
    }

}