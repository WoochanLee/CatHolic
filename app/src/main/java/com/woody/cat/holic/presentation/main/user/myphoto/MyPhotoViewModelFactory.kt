package com.woody.cat.holic.presentation.main.user.myphoto

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.posting.GetUserUploadedPostings
import com.woody.cat.holic.usecase.posting.RemoveUserPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile

class MyPhotoViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPhotoViewModel::class.java)) {
            return MyPhotoViewModel(
                GetCurrentUserId(userRepository),
                GetUserUploadedPostings(postingRepository),
                RemoveUserPosting(postingRepository),
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}