package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.User

data class UserItem(
    val userId: String,
    val displayName: MutableLiveData<String?> = MutableLiveData("..."),
    val userProfilePhotoUrl: MutableLiveData<String?> = MutableLiveData(),
    val postingCount: MutableLiveData<String?> = MutableLiveData("-"),
    val followerCount: MutableLiveData<String?> = MutableLiveData("-"),
    val followerUserIds: MutableLiveData<List<String>> = MutableLiveData(emptyList()),
    val followingCount: MutableLiveData<String?> = MutableLiveData("-"),
    val blocked: MutableLiveData<Boolean> = MutableLiveData(false)
)

fun User.updateUserItem(userItem: UserItem) {
    userItem.displayName.postValue(displayName)
    userItem.userProfilePhotoUrl.postValue(userProfilePhotoUrl)
    userItem.postingCount.postValue(postingCount.toString())
    userItem.followerCount.postValue(followerCount.toString())
    userItem.followingCount.postValue(followingCount.toString())
    userItem.followerUserIds.postValue(followerUserIds)
    userItem.blocked.postValue(blocked)
}