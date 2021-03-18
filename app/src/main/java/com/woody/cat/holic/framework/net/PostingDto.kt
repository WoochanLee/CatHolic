package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.ServerTimestamp
import com.woody.cat.holic.domain.Posting
import java.util.*

data class PostingDto(
    val userId: String? = null,
    val downloadUrl: String? = null,
    val stared: Int = 0,
    val reported: Int = 0,

    @ServerTimestamp
    val created: Date? = null,

    @ServerTimestamp
    val updated: Date? = null
)

fun Posting.mapToPostingDto(): PostingDto {
    return PostingDto(
        userId = userId,
        downloadUrl = downloadUrl,
        stared = stared,
        reported = reported,
        created = null,
        updated = null
    )
}

fun PostingDto.mapToPosting(): Posting {
    return Posting(
        userId = userId ?: "",
        downloadUrl = downloadUrl ?: "",
        stared = stared,
        reported = reported,
        created = created.toString(),
        updated = updated.toString()
    )
}