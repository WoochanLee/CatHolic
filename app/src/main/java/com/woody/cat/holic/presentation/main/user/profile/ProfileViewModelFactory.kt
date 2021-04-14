package com.woody.cat.holic.presentation.main.user.profile

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.user.UpdateUserProfile

class ProfileViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                GetCurrentUserId(userRepository),
                GetUserProfile(userRepository),
                UploadPhoto(photoRepository),
                UpdateUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}