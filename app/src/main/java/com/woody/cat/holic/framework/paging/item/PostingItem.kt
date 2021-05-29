package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.Posting

abstract class RecyclerViewItem(open val postingId: String)

data class PostingItem(
    val user: UserItem,
    val imageUrls: List<String>,
    val likedUserIds: List<String>,
    val likeCount: MutableLiveData<Int>,
    val currentUserLiked: MutableLiveData<Boolean>,
    val commentIds: List<String>,
    val commentCount: MutableLiveData<Int>,
    val currentUserCommented: MutableLiveData<Boolean>,
    val reportCount: Int,
    override val postingId: String,
    val created: String,
    val updated: String,
    val deleted: Boolean,
    val imagePositionText: MutableLiveData<String>
) : RecyclerViewItem(postingId)

class AdItem(postingId: String) : RecyclerViewItem(postingId)

fun Posting.mapToPostingItem(currentUserId: String?, displayName: String? = null): PostingItem {
    return PostingItem(
        user = UserItem(
            userId = userId,
            displayName = if (displayName != null) MutableLiveData(displayName) else MutableLiveData("...")
        ),
        imageUrls = imageUrls,
        likedUserIds = likedUserIds,
        likeCount = MutableLiveData(likeCount),
        currentUserLiked = if (currentUserId != null) MutableLiveData(likedUserIds.contains(currentUserId)) else MutableLiveData(false),
        commentIds = commentIds,
        commentCount = MutableLiveData(commentCount),
        currentUserCommented = if (currentUserId != null) MutableLiveData(commentUserIds.contains(currentUserId)) else MutableLiveData(false),
        reportCount = reportCount,
        postingId = postingId ?: "",
        created = created ?: "",
        updated = updated ?: "",
        deleted = deleted,
        imagePositionText = MutableLiveData("1 / ${imageUrls.size}")
    )
}