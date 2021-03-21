package com.woody.cat.holic.presentation.main.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.user.FirebaseUserManager

class GalleryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(FirebaseUserManager, CatHolicApplication.application.postingRepository) as T
        } else {
            throw IllegalStateException()
        }
    }
}