package com.woody.cat.holic.presentation.main.posting

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.posting.UpdateLikedPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId

class PostingViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostingViewModel::class.java)) {
            return PostingViewModel(
                refreshEventBus,
                GetCurrentUserId(userRepository),
                UpdateLikedPosting(likePostingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}