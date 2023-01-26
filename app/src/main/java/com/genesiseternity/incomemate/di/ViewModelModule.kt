package com.genesiseternity.incomemate.di

import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.auth.LoginViewModel
import com.genesiseternity.incomemate.auth.RegisterViewModel
import com.genesiseternity.incomemate.history.OperationsHistoryViewModel
import com.genesiseternity.incomemate.pieChart.PieChartFragmentViewModel
import com.genesiseternity.incomemate.pieChart.PieChartHeadViewModel
import com.genesiseternity.incomemate.wallet.WalletViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule
{
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(viewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun bindWalletViewModel(viewModel: WalletViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PieChartHeadViewModel::class)
    abstract fun bindPieChartHeadViewModel(viewModel: PieChartHeadViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PieChartFragmentViewModel::class)
    abstract fun bindPieChartFragmentViewModel(viewModel: PieChartFragmentViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(OperationsHistoryViewModel::class)
    abstract fun bindOperationsHistoryViewModel(viewModel: OperationsHistoryViewModel): ViewModel
}