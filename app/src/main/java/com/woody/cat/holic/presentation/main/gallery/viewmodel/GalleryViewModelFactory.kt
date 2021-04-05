package com.woody.cat.holic.presentation.main.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetGalleryPostings
import com.woody.cat.holic.usecase.user.GetUserProfile

class GalleryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(
                ChangeToNextPostingOrder(CatHolicApplication.application.postingRepository),
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                GetGalleryPostings(CatHolicApplication.application.postingRepository),
                GetUserProfile(CatHolicApplication.application.userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}