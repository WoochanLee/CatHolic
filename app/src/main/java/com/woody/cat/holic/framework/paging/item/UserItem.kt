package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData

data class UserItem(
    val userId: String,
    val displayName: MutableLiveData<String?> = MutableLiveData("..."),
    val userProfilePhotoUrl: MutableLiveData<String?> = MutableLiveData(),
    val postingCount: MutableLiveData<String?> = MutableLiveData("-"),
    val followerCount: MutableLiveData<String?> = MutableLiveData("-"),
    val followingCount: MutableLiveData<String?> = MutableLiveData("-"),
)