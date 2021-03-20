package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.ServerTimestamp
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.domain.User
import java.util.*

data class PostingDto(
    val user: UserDto? = null,
    val downloadUrl: String? = null,
    val liked: Int = 0,
    val reported: Int = 0,

    @ServerTimestamp
    val created: Date? = null,

    @ServerTimestamp
    val updated: Date? = null
) {
    data class UserDto(
        val userId: String = "",
        val displayName: String = "",
        val userPhotoUrl: String? = null,
    )
}

fun Posting.mapToPostingDto(): PostingDto {
    return PostingDto(
        user = user.mapToUserDto(),
        downloadUrl = downloadUrl,
        liked = liked,
        reported = reported,
        created = null,
        updated = null
    )
}

fun User.mapToUserDto(): PostingDto.UserDto {
    return PostingDto.UserDto(
        userId = userId,
        displayName = displayName,
        userPhotoUrl = userPhotoUrl
    )
}

fun PostingDto.mapToPosting(): Posting {
    return Posting(
        user = user?.mapToUser() ?: User("", ""),
        downloadUrl = downloadUrl ?: "",
        liked = liked,
        reported = reported,
        created = created.toString(),
        updated = updated.toString()
    )
}

fun PostingDto.UserDto.mapToUser(): User {
    return User(
        userId = userId,
        displayName = displayName,
        userPhotoUrl = userPhotoUrl
    )
}