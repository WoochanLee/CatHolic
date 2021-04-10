package com.woody.cat.holic.presentation.main.like.viewmodel

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.posting.GetUserLikePostings
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile

class LikeViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeViewModel::class.java)) {
            return LikeViewModel(
                ChangeToNextPostingOrder(postingRepository),
                GetCurrentUserId(userRepository),
                GetUserLikePostings(postingRepository),
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}