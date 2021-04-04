package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.ServerTimestamp
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.makePostingDateString
import java.util.*

data class PostingDto(
    val userId: String = "",
    val downloadUrl: String? = null,
    val liked: Int = 0,
    val likedUserIds: List<String> = listOf(),
    val reported: Int = 0,
    val reportedUserIds: List<String> = listOf(),

    @ServerTimestamp
    val created: Date? = null,

    @ServerTimestamp
    val updated: Date? = null
)

fun Posting.mapToPostingDto(): PostingDto {
    return PostingDto(
        userId = userId,
        downloadUrl = downloadUrl,
        liked = liked,
        likedUserIds = likedUserIds,
        reported = reported,
        reportedUserIds = reportedUserIds,
        created = null,
        updated = null
    )
}

fun PostingDto.mapToPosting(postingId: String): Posting {
    return Posting(
        userId = userId,
        downloadUrl = downloadUrl ?: "",
        liked = liked,
        likedUserIds = likedUserIds,
        reported = reported,
        reportedUserIds = reportedUserIds,
        postingId = postingId,
        created = created.makePostingDateString(),
        updated = updated.makePostingDateString()
    )
}