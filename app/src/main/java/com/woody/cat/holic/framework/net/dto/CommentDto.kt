package com.woody.cat.holic.framework.net.dto

import com.google.firebase.firestore.ServerTimestamp
import com.woody.cat.holic.domain.Comment
import com.woody.cat.holic.framework.base.makeCommentDateString
import com.woody.cat.holic.framework.base.makePostingDateString
import java.util.*

data class CommentDto(
    val postingId: String = "",
    val userId: String = "",
    val commentEmojis: String = "",

    @ServerTimestamp
    val created: Date? = null,

    @ServerTimestamp
    val updated: Date? = null
)

fun Comment.mapToCommentDto(): CommentDto {
    return CommentDto(
        postingId = postingId,
        userId = userId,
        commentEmojis = commentEmojis
    )
}

fun CommentDto.mapToComment(commentId: String): Comment {
    return Comment(
        commentId = commentId,
        postingId = postingId,
        userId = userId,
        commentEmojis = commentEmojis,
        created = created.makeCommentDateString(),
        updated = updated.makeCommentDateString()
    )
}