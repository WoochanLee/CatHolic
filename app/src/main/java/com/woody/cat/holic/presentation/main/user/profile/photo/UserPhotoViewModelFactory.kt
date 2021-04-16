package com.woody.cat.holic.presentation.main.user.profile.photo

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.posting.GetUserPostings
import com.woody.cat.holic.usecase.user.GetUserProfile

class UserPhotoViewModelFactory(private val userId: String) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPhotoViewModel::class.java)) {
            return UserPhotoViewModel(
                userId,
                GetUserPostings(postingRepository),
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}