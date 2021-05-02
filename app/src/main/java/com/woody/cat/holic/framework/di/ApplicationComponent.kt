package com.woody.cat.holic.framework.di

import com.woody.cat.holic.framework.CatHolicApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        RepositoryModule::class,
        UseCaseModule::class,
        EventBusModule::class,
        ActivitiesModule::class,
        FragmentsModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<CatHolicApplication> {

    /*@Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }*/
/*
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }*/
}