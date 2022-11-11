package com.genesiseternity.incomemate.di

import androidx.lifecycle.ViewModel
import com.genesiseternity.incomemate.auth.LoginViewModel
import com.genesiseternity.incomemate.auth.RegisterViewModel
import com.genesiseternity.incomemate.history.OperationsHistoryViewModel
import com.genesiseternity.incomemate.pieChart.PieChartViewModel
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
    @ViewModelKey(PieChartViewModel::class)
    abstract fun bindPieChartViewModel(viewModel: PieChartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WalletViewModel::class)
    abstract fun bindWalletViewModel(viewModel: WalletViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OperationsHistoryViewModel::class)
    abstract fun bindOperationsHistoryViewModel(viewModel: OperationsHistoryViewModel): ViewModel
}