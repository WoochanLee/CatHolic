package com.woody.cat.holic.presentation.main.user.myphoto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.posting.GetUserUploadedPostings
import com.woody.cat.holic.usecase.posting.RemoveUserPosting

class MyPhotoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPhotoViewModel::class.java)) {
            return MyPhotoViewModel(
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                GetUserUploadedPostings(CatHolicApplication.application.postingRepository),
                RemoveUserPosting(CatHolicApplication.application.postingRepository),
                GetUserProfile(CatHolicApplication.application.userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}