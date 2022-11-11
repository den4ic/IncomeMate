package com.genesiseternity.incomemate.app

import com.genesiseternity.incomemate.di.DaggerAppComponent
import com.genesiseternity.incomemate.di.RoomModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    private val applicationInjector = DaggerAppComponent
        .builder()
        .application(this)
        .roomModule(RoomModule(this))
        .build()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector
}
