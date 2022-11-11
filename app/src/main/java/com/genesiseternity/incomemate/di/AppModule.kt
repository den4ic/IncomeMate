package com.genesiseternity.incomemate.di

import android.app.Application
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
}