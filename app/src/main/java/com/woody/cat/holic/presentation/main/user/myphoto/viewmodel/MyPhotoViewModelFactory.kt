package com.woody.cat.holic.presentation.main.user.myphoto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.usecase.GetUserUploadedPostings
import com.woody.cat.holic.usecase.RemoveUserPosting

class MyPhotoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPhotoViewModel::class.java)) {
            return MyPhotoViewModel(
                FirebaseUserManager,
                GetUserUploadedPostings(CatHolicApplication.application.postingRepository),
                RemoveUserPosting(CatHolicApplication.application.postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}