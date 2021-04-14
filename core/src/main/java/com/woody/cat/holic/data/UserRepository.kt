package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.User

interface UserRepository {

    fun isSignedIn(): Boolean

    fun getCurrentUserId(): String?

    suspend fun addUserProfile(user: User): Resource<Unit>

    suspend fun getUserProfile(userId: String): Resource<User>

    suspend fun followUser(myUserId: String, targetUserId: String): Resource<Unit>

    suspend fun unfollowUser(myUserId: String, targetUserId: String): Resource<Unit>

    suspend fun getFollowers(userId: String): Resource<List<String>>

    suspend fun getFollowings(userId: String): Resource<List<String>>
}