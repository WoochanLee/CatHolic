package com.woody.cat.holic.presentation.main.like.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetUserLikePostings
import com.woody.cat.holic.usecase.user.GetUserProfile

class LikeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeViewModel::class.java)) {
            return LikeViewModel(
                ChangeToNextPostingOrder(CatHolicApplication.application.postingRepository),
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                GetUserLikePostings(CatHolicApplication.application.postingRepository),
                GetUserProfile(CatHolicApplication.application.userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}