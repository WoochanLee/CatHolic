package com.woody.cat.holic.framework.di

import com.woody.cat.holic.framework.base.RefreshEventBus
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class EventBusModule {

    @Provides
    @Singleton
    fun provideRefreshEventBus(): RefreshEventBus = RefreshEventBus()
}