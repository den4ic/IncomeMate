package com.genesiseternity.incomemate.di

import com.genesiseternity.incomemate.InfoActivity
import com.genesiseternity.incomemate.MainActivity
import com.genesiseternity.incomemate.auth.LoginActivity
import com.genesiseternity.incomemate.auth.RegisterActivity
import com.genesiseternity.incomemate.di.scope.ActivityScope
import com.genesiseternity.incomemate.pieChart.CategoryActivity
import com.genesiseternity.incomemate.settings.Passcode
import com.genesiseternity.incomemate.wallet.CurrencyActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    abstract fun contributeRegisterActivity(): RegisterActivity

    //region for CurrencyFormatModule
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCategoryActivity(): CategoryActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCurrencyActivity(): CurrencyActivity
    //endregion

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributePasscode(): Passcode

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeInfoActivity(): InfoActivity
}