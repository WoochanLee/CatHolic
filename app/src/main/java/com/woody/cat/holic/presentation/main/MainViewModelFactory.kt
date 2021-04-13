package com.woody.cat.holic.presentation.main

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.posting.AddLikeInPosting
import com.woody.cat.holic.usecase.posting.GetPostingOrder
import com.woody.cat.holic.usecase.posting.RemoveLikeInPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn

class MainViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                refreshEventBus,
                GetPostingOrder(postingRepository),
                GetIsSignedIn(userRepository),
                GetCurrentUserId(userRepository),
                AddLikeInPosting(postingRepository),
                RemoveLikeInPosting(postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}