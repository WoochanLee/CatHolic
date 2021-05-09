package com.woody.cat.holic.framework.di

import com.woody.cat.holic.presentation.service.download.PhotoDownloadService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServicesModule {

    @ContributesAndroidInjector
    abstract fun contributePhotoDownloadService(): PhotoDownloadService
}