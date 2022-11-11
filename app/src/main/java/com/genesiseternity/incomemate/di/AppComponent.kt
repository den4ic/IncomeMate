package com.genesiseternity.incomemate.di

import android.app.Application
import android.content.Context
import com.genesiseternity.incomemate.app.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [],
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        FragmentBuildersModule::class,
        RoomModule::class,
        AppModule::class,
        NetworkModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        fun application(application: Application): Builder

        fun roomModule(roomModule: RoomModule): Builder

        fun build(): AppComponent
    }
}