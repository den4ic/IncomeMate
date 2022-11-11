package com.genesiseternity.incomemate.di

import com.genesiseternity.incomemate.retrofit.CurrencyCbrModel
import com.genesiseternity.incomemate.retrofit.CurrencyConverterCbrApi
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit
    {
        return Retrofit.Builder().baseUrl(CurrencyCbrModel.BASE_URL)
            //addConverterFactory(SimpleXmlConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideCurrencyConverterCbrApi(retrofit: Retrofit): CurrencyConverterCbrApi
    {
        return retrofit.create(CurrencyConverterCbrApi::class.java)
    }
}