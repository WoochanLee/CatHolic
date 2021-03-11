package com.woody.cat.holic.framework

import android.app.Application
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.interactors.GetPhotos
import com.woody.cat.holic.interactors.UploadPhoto

class CatHolicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val photoRepository = PhotoRepository(FirebaseStoragePhotoDataSource())

        BaseViewModelFactory.inject(
            Interactors(
                GetPhotos(photoRepository),
                UploadPhoto(photoRepository)
            )
        )
    }
}