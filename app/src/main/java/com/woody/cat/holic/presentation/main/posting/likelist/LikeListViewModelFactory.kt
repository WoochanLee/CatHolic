package com.woody.cat.holic.presentation.main.posting.likelist

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.user.GetUserProfile

class LikeListViewModelFactory(private val likeUserList: List<String>) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeListViewModel::class.java)) {
            return LikeListViewModel(
                likeUserList,
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}