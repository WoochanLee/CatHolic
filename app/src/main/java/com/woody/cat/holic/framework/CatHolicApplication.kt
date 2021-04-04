package com.woody.cat.holic.framework

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.framework.base.AlbumMediaLoader
import com.woody.cat.holic.framework.db.SettingRepositoryImpl
import com.woody.cat.holic.framework.photo.PhotoRepositoryImpl
import com.woody.cat.holic.framework.posting.PostingRepositoryImpl
import com.woody.cat.holic.framework.user.UserRepositoryImpl
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import java.util.*

class CatHolicApplication : Application() {

    companion object {
        lateinit var application: CatHolicApplication

        private const val NAME_SETTING_REPOSITORY = "SETTING_REPOSITORY"
    }

    private lateinit var settingSharedPreferences: SharedPreferences

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorageReference: StorageReference

    val settingRepository by lazy { SettingRepositoryImpl(settingSharedPreferences) }
    val photoRepository by lazy { PhotoRepositoryImpl(firebaseStorageReference) }
    val postingRepository by lazy { PostingRepositoryImpl(firebaseFirestore) }
    val userRepository by lazy { UserRepositoryImpl(firebaseFirestore, firebaseAuth) }

    override fun onCreate() {
        super.onCreate()
        application = this

        initSharedPreference()
        initFirebase()
        initLibraryAlbum()
        initSetting()
    }

    private fun initSharedPreference() {
        settingSharedPreferences = getSharedPreferences(NAME_SETTING_REPOSITORY, Context.MODE_PRIVATE);
    }

    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorageReference = FirebaseStorage.getInstance().apply {
            maxDownloadRetryTimeMillis = PhotoRepositoryImpl.MAX_RETRY_TIME_MILLIS
            maxUploadRetryTimeMillis = PhotoRepositoryImpl.MAX_RETRY_TIME_MILLIS
        }.reference
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
}