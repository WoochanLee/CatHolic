package com.woody.cat.holic.presentation.main.user.profile

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.user.UpdateFollowUser
import com.woody.cat.holic.usecase.user.UpdateUserProfile

class ProfileViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                refreshEventBus,
                GetCurrentUserId(userRepository),
                GetUserProfile(userRepository),
                GetAppSetting(settingRepository),
                UploadPhoto(photoRepository),
                UpdateUserProfile(userRepository),
                UpdateFollowUser(followRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}