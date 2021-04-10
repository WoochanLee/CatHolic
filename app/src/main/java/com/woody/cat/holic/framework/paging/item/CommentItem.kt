package com.woody.cat.holic.framework.paging.item

import com.woody.cat.holic.domain.Comment

data class CommentItem(
    val commentId: String,
    val postingId: String,
    val user: UserItem,
    val commentEmojis: String,
    val created: String,
    val updated: String
)

fun Comment.mapToCommentItem(): CommentItem {
    return CommentItem(
        commentId = commentId ?: "",
        postingId = postingId,
        user = UserItem(userId = userId),
        commentEmojis = commentEmojis,
        created = created ?: "",
        updated = updated ?: ""
    )
}