package com.genesiseternity.incomemate.di

import com.genesiseternity.incomemate.CurrencyFormat
import dagger.Module
import dagger.Provides

@Module
abstract class CurrencyFormatModule {
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