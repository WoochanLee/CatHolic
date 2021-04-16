package com.woody.cat.holic.presentation.main.user.profile.following

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.user.GetUserProfile

class FollowingListViewModelFactory(private val followingUserList: List<String>) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowingListViewModel::class.java)) {
            return FollowingListViewModel(
                followingUserList,
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}