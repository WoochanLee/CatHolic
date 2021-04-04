package com.woody.cat.holic.domain

data class Posting(
    val userId: String,
    val downloadUrl: String,
    val liked: Int = 0,
    val likedUserIds: List<String> = listOf(),
    val reported: Int = 0,
    val reportedUserIds: List<String> = listOf(),
    val postingId: String = "",
    val created: String = "",
    val updated: String = ""
)