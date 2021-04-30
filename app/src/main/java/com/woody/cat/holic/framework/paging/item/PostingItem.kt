package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.Posting

abstract class RecyclerViewItem(open val postingId: String)

data class PostingItem(
    val user: UserItem,
    val downloadUrl: String,
    val likedUserIds: List<String>,
    val likeCount: MutableLiveData<Int>,
    val currentUserLiked: MutableLiveData<Boolean>,
    val commentCount: MutableLiveData<Int>,
    val reportCount: Int,
    override val postingId: String,
    val created: String,
    val updated: String
): RecyclerViewItem(postingId)

class AdItem(postingId: String): RecyclerViewItem(postingId)

fun Posting.mapToPostingItem(currentUserId: String?): PostingItem {
    return PostingItem(
        user = UserItem(userId = userId),
        downloadUrl = downloadUrl,
        likedUserIds = likedUserIds,
        likeCount = MutableLiveData(likeCount),
        currentUserLiked = if (currentUserId != null) MutableLiveData(likedUserIds.contains(currentUserId)) else MutableLiveData(false),
        commentCount = MutableLiveData(commentCount),
        reportCount = reportCount,
        postingId = postingId ?: "",
        created = created ?: "",
        updated = updated ?: ""
    )
}