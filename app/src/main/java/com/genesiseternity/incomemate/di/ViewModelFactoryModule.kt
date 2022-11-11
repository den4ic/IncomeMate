package com.genesiseternity.incomemate.di

import androidx.lifecycle.ViewModelProvider
import com.genesiseternity.incomemate.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule
{
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}