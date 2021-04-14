package com.woody.cat.holic.framework.net.dto

import com.google.firebase.firestore.ServerTimestamp
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.makePostingDateString
import java.util.*

data class PostingDto(
    val userId: String = "",
    val downloadUrl: String? = null,
    val likeCount: Int = 0,
    val likedUserIds: List<String> = listOf(),
    val reportCount: Int = 0,
    val reportedUserIds: List<String> = listOf(),
    val commentCount: Int = 0,
    val commentIds: List<String> = listOf(),
    val deleted: Boolean = false,

    @ServerTimestamp
    val created: Date? = null,

    @ServerTimestamp
    val updated: Date? = null
)

fun Posting.mapToPostingDto(): PostingDto {
    return PostingDto(
        userId = userId,
        downloadUrl = downloadUrl,
        likeCount = likeCount,
        likedUserIds = likedUserIds,
        reportCount = reportCount,
        reportedUserIds = reportedUserIds,
        commentCount = commentCount,
        commentIds = commentIds,
        created = null,
        updated = null
    )
}

fun PostingDto.mapToPosting(postingId: String): Posting {
    return Posting(
        userId = userId,
        downloadUrl = downloadUrl ?: "",
        likeCount = likeCount,
        likedUserIds = likedUserIds,
        reportCount = reportCount,
        reportedUserIds = reportedUserIds,
        commentCount = commentCount,
        commentIds = commentIds,
        postingId = postingId,
        created = created.makePostingDateString(),
        updated = updated.makePostingDateString()
    )
}