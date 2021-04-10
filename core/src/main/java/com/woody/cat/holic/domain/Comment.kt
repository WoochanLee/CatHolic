package com.woody.cat.holic.domain

data class Comment(
    val commentId: String? = null,
    val postingId: String,
    val userId: String,
    val commentEmojis: String,
    val created: String? = null,
    val updated: String? = null
)