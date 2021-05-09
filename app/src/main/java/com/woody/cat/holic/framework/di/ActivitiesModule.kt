package com.woody.cat.holic.framework.di

import com.woody.cat.holic.presentation.main.MainActivity
import com.woody.cat.holic.presentation.main.user.myphoto.MyPhotoActivity
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoActivity
import com.woody.cat.holic.presentation.splash.SplashActivity
import com.woody.cat.holic.presentation.upload.UploadActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeUserPhotoActivity(): UserPhotoActivity

    @ContributesAndroidInjector
    abstract fun contributeProfileActivity(): ProfileActivity

    @ContributesAndroidInjector
    abstract fun contributeUploadActivity(): UploadActivity

    @ContributesAndroidInjector
    abstract fun contributeMyPhotoActivity(): MyPhotoActivity
}