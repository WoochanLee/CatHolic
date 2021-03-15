package com.woody.cat.holic.framework

import android.app.Application
import com.woody.cat.holic.framework.base.AlbumMediaLoader
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import java.util.*

class CatHolicApplication : Application() {

    companion object {
        lateinit var application: CatHolicApplication
    }

    val photoRepository by lazy { PhotoRepositoryImpl() }
    val postingRepository by lazy { PostingRepositoryImpl() }

    override fun onCreate() {
        super.onCreate()

        application = this
        initLibraryAlbum()
    }

    private fun initLibraryAlbum() {
        Album.initialize(
            AlbumConfig.newBuilder(this)
                .setAlbumLoader(AlbumMediaLoader())
                .setLocale(Locale.KOREAN)
                .build()
        )
    }
}