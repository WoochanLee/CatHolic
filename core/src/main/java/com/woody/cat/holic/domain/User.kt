package com.woody.cat.holic.domain

data class User(
    val userId: String,
    var displayName: String? = null,
    var userProfilePhotoUrl: String? = null,
    var userBackgroundPhotoUrl: String? = null,
    val greetings: String? = null,
    val postingCount: Int = 0,
    val followerCount: Int = 0,
    val followerUserIds: List<String> = listOf(),
    val followingCount: Int = 0,
    val followingUserIds: List<String> = listOf(),
)