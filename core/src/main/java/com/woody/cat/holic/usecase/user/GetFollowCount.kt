package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.FollowRepository

class GetFollowCount(private val followRepository: FollowRepository) {

    suspend fun getFollowers(userId: String) = followRepository.getFollowers(userId)

    suspend fun getFollowings(userId: String) = followRepository.getFollowings(userId)
}