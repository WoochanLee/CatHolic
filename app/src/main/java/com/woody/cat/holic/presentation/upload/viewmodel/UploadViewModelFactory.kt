package com.woody.cat.holic.presentation.upload.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.photo.DetectCatFromPhoto
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.posting.AddPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId

class UploadViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                DetectCatFromPhoto(CatHolicApplication.application.photoAnalyzer),
                UploadPhoto(CatHolicApplication.application.photoRepository),
                AddPosting(CatHolicApplication.application.postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}