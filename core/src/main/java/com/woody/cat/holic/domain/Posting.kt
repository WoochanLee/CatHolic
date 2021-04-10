package com.woody.cat.holic.domain

data class Posting(
    val postingId: String? = null,
    val userId: String,
    val downloadUrl: String,
    val liked: Int = 0,
    val likedUserIds: List<String> = listOf(),
    val reported: Int = 0,
    val reportedUserIds: List<String> = listOf(),
    val commented: Int = 0,
    val commentIds: List<String> = listOf(),
    val created: String? = null,
    val updated: String? = null
)