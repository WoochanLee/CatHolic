package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository

class GetUserProfile(private val userRepository: UserRepository) {

    suspend operator fun invoke(userId: String) = userRepository.getUserProfile(userId)
}