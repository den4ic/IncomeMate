package com.genesiseternity.incomemate.di

import com.genesiseternity.incomemate.di.scope.FragmentScope
import com.genesiseternity.incomemate.history.OperationsHistoryFragment
import com.genesiseternity.incomemate.pieChart.PieChartHeadFragment
import com.genesiseternity.incomemate.pieChart.PieChartFragmentView
import com.genesiseternity.incomemate.settings.SettingsFragment
import com.genesiseternity.incomemate.wallet.WalletFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule
{
    @FragmentScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeWalletFragment(): WalletFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributePieChartHeadFragment(): PieChartHeadFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributePieChartFragmentView(): PieChartFragmentView

    @FragmentScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeOperationsHistoryFragment(): OperationsHistoryFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
}