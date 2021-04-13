package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.photo.DetectCatFromPhoto
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.posting.AddPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId

class UploadViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(
                refreshEventBus,
                GetCurrentUserId(userRepository),
                DetectCatFromPhoto(photoAnalyzer),
                UploadPhoto(photoRepository),
                AddPosting(postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}