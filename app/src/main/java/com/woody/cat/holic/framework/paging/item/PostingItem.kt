package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.Posting

data class PostingItem(
    val user: UserItem,
    val downloadUrl: String,
    val likedUserIds: List<String>,
    val liked: MutableLiveData<Int>,
    val currentUserLiked: MutableLiveData<Boolean>,
    val commented: MutableLiveData<Int>,
    val reported: Int,
    val postingId: String,
    val created: String,
    val updated: String
)

fun Posting.mapToPostingItem(currentUserId: String?): PostingItem {
    return PostingItem(
        user = UserItem(userId = userId),
        downloadUrl = downloadUrl,
        likedUserIds = likedUserIds,
        liked = MutableLiveData(liked),
        currentUserLiked = if (currentUserId != null) MutableLiveData(likedUserIds.contains(currentUserId)) else MutableLiveData(false),
        commented = MutableLiveData(commented),
        reported = reported,
        postingId = postingId ?: "",
        created = created ?: "",
        updated = updated ?: ""
    )
}