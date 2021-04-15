package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.User

interface UserRepository {

    fun isSignedIn(): Boolean

    fun getCurrentUserId(): String?

    suspend fun addUserProfile(user: User): Resource<Unit>

    suspend fun getUserProfile(userId: String): Resource<User>

    suspend fun updateDisplayName(userId: String, displayName: String): Resource<Unit>

    suspend fun updateUserProfilePhotoUrl(userId: String, userProfilePhotoUrl: String): Resource<Unit>

    suspend fun updateUserBackgroundPhotoUrl(userId: String, userBackgroundPhotoUrl: String): Resource<Unit>

    suspend fun updateGreetings(userId: String, greetings: String): Resource<Unit>
}