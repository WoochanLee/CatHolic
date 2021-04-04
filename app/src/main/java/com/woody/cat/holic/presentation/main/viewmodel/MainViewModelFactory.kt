package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.posting.AddLikeInPosting
import com.woody.cat.holic.usecase.posting.GetPostingOrder
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import com.woody.cat.holic.usecase.posting.RemoveLikeInPosting

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                GetPostingOrder(CatHolicApplication.application.postingRepository),
                GetIsSignedIn(CatHolicApplication.application.userRepository),
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                AddLikeInPosting(CatHolicApplication.application.postingRepository),
                RemoveLikeInPosting(CatHolicApplication.application.postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}