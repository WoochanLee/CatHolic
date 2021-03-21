package com.woody.cat.holic.presentation.main

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.domain.User

data class PostingItem(
    val user: User,
    val downloadUrl: String,
    val likedUserIds: List<String>,
    val liked: MutableLiveData<Int>,
    val currentUserLiked: MutableLiveData<Boolean>,
    val reported: Int,
    val postingId: String,
    val created: String,
    val updated: String
)

fun Posting.mapToPostingItem(currentUserId: String?): PostingItem {
    return PostingItem(
        user = user,
        downloadUrl = downloadUrl,
        likedUserIds = likedUserIds,
        liked = MutableLiveData(liked),
        currentUserLiked = if (currentUserId != null) MutableLiveData(likedUserIds.contains(currentUserId)) else MutableLiveData(false),
        reported = reported,
        postingId = postingId,
        created = created,
        updated = updated
    )
}