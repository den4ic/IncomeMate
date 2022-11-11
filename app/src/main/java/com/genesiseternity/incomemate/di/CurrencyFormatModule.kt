package com.genesiseternity.incomemate.di

import com.genesiseternity.incomemate.CurrencyFormat
import com.genesiseternity.incomemate.di.scope.ActivityScope
import com.genesiseternity.incomemate.pieChart.CategoryActivity
import com.genesiseternity.incomemate.wallet.CurrencyActivity
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
abstract class CurrencyFormatModule {

    //@Provides
    //@Singleton
    //fun provideCurrencyFormat() : CurrencyFormat
    //{
    //    return CurrencyFormat()
    //}

    //@Module
    companion object {
        //@JvmStatic
        @Provides
        fun provideCurrencyFormat() : CurrencyFormat
        {
            return CurrencyFormat()
        }
    }

}