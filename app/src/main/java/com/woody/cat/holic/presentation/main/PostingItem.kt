package com.woody.cat.holic.presentation.main

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.domain.User

data class PostingItem(
    val user: UserItem,
    val downloadUrl: String,
    val likedUserIds: List<String>,
    val liked: MutableLiveData<Int>,
    val currentUserLiked: MutableLiveData<Boolean>,
    val reported: Int,
    val postingId: String,
    val created: String,
    val updated: String
)

fun mapToPostingItem(posting: Posting, currentUserId: String?): PostingItem {
    return PostingItem(
        user = UserItem(posting.userId),
        downloadUrl = posting.downloadUrl,
        likedUserIds = posting.likedUserIds,
        liked = MutableLiveData(posting.liked),
        currentUserLiked = if (currentUserId != null) MutableLiveData(posting.likedUserIds.contains(currentUserId)) else MutableLiveData(false),
        reported = posting.reported,
        postingId = posting.postingId,
        created = posting.created,
        updated = posting.updated
    )
}