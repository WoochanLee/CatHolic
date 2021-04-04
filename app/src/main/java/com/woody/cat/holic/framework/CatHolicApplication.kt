package com.woody.cat.holic.framework

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.framework.base.AlbumMediaLoader
import com.woody.cat.holic.framework.photo.PhotoRepositoryImpl
import com.woody.cat.holic.framework.posting.PostingRepositoryImpl
import com.woody.cat.holic.framework.user.UserRepositoryImpl
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import java.util.*

class CatHolicApplication : Application() {

    companion object {
        lateinit var application: CatHolicApplication
    }

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseStorageReference: StorageReference

    val photoRepository by lazy { PhotoRepositoryImpl(firebaseStorageReference) }
    val postingRepository by lazy { PostingRepositoryImpl(firebaseFirestore) }
    val userRepository by lazy { UserRepositoryImpl(firebaseFirestore, firebaseAuth) }

    override fun onCreate() {
        super.onCreate()

        initFirebase()
        application = this
        initLibraryAlbum()
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
}