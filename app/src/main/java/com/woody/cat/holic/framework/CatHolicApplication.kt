package com.woody.cat.holic.framework

import androidx.appcompat.app.AppCompatDelegate
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import com.woody.cat.holic.data.SettingRepository
import com.woody.cat.holic.framework.base.AlbumMediaLoader
import com.woody.cat.holic.framework.di.ApplicationModule
import com.woody.cat.holic.framework.di.DaggerApplicationComponent
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.util.*
import javax.inject.Inject


class CatHolicApplication : DaggerApplication() {

    companion object {
        lateinit var application: CatHolicApplication
    }

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onCreate() {
        super.onCreate()
        application = this

        initLibraryAlbum()
        initSetting()
        initEmoji()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    private fun initLibraryAlbum() {
        Album.initialize(
            AlbumConfig.newBuilder(this)
                .setAlbumLoader(AlbumMediaLoader())
                .setLocale(Locale.KOREAN)
                .build()
        )
    }

    private fun initSetting() {
        val isDarkMode = settingRepository.getDarkMode()
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun initEmoji() {
        EmojiManager.install(TwitterEmojiProvider())
    }
}