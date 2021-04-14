package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface FollowRepository {
    suspend fun followUser(myUserId: String, targetUserId: String): Resource<Unit>

    suspend fun unfollowUser(myUserId: String, targetUserId: String): Resource<Unit>

    suspend fun getFollowers(userId: String): Resource<List<String>>

    suspend fun getFollowings(userId: String): Resource<List<String>>
}