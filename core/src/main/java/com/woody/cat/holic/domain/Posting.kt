package com.woody.cat.holic.domain

data class Posting(
    val postingId: String? = null,
    val userId: String,
    val imageUrls: List<String> = listOf(),
    val likeCount: Int = 0,
    val likedUserIds: List<String> = listOf(),
    val reportCount: Int = 0,
    val reportedUserIds: List<String> = listOf(),
    val commentCount: Int = 0,
    val commentIds: List<String> = listOf(),
    val commentUserIds: List<String> = listOf(),
    val created: String? = null,
    val updated: String? = null
)