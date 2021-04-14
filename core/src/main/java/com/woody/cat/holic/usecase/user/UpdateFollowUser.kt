package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.FollowRepository

class UpdateFollowUser(private val followRepository: FollowRepository) {

    suspend fun followUser(myUserId: String, targetUserId: String) = followRepository.followUser(myUserId, targetUserId)

    suspend fun unfollowUser(myUserId: String, targetUserId: String) = followRepository.unfollowUser(myUserId, targetUserId)
}