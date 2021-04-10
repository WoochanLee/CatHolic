package com.woody.cat.holic.framework.net.dto

import com.woody.cat.holic.domain.User

data class UserDto(
    val userId: String = "",
    val displayName: String = "",
    val userPhotoUrl: String? = null,
)

fun UserDto.mapToUser(): User {
    return User(
        userId = userId,
        displayName = displayName,
        userPhotoUrl = userPhotoUrl
    )
}
