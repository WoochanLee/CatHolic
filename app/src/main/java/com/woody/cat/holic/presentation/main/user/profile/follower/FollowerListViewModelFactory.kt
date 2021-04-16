package com.woody.cat.holic.presentation.main.user.profile.follower

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.user.GetUserProfile

class FollowerListViewModelFactory(private val followerUserList: List<String>) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowerListViewModel::class.java)) {
            return FollowerListViewModel(
                followerUserList,
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}