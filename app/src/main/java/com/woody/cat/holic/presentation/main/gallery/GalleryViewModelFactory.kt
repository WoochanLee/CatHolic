package com.woody.cat.holic.presentation.main.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.GetPhotos

class GalleryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(GetPhotos(CatHolicApplication.application.photoRepository)) as T
        } else {
            throw IllegalStateException()
        }
    }
}