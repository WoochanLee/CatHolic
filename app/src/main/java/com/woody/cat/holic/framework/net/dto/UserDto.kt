package com.woody.cat.holic.framework.net.dto

import com.woody.cat.holic.domain.User

data class UserDto(
    val userId: String = "",
    val displayName: String = "",
    val userPhotoUrl: String? = null,
    val userBackgroundUrl: String? = null,
    val greetings: String? = null,
    val postingCount: Int = 0,
    val followerCount: Int = 0,
    val followerUserIds: List<String> = listOf(),
    val followingCount: Int = 0,
    val followingUserIds: List<String> = listOf()
)

fun UserDto.mapToUser(): User {
    return User(
        userId = userId,
        displayName = displayName,
        userPhotoUrl = userPhotoUrl,
        userBackgroundPhotoUrl = userBackgroundUrl,
        greetings = greetings,
        postingCount = postingCount,
        followerCount = followerCount,
        followerUserIds = followerUserIds,
        followingCount = followingCount,
        followingUserIds = followingUserIds
    )
}
