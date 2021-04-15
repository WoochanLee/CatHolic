package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository

class UpdateUserProfile(private val userRepository: UserRepository) {

    suspend fun updateDisplayName(userId: String, displayName: String) = userRepository.updateDisplayName(userId, displayName)

    suspend fun updateUserProfilePhotoUrl(userId: String, userProfilePhotoUrl: String) = userRepository.updateUserProfilePhotoUrl(userId, userProfilePhotoUrl)

    suspend fun updateUserBackgroundPhotoUrl(userId: String, userBackgroundPhotoUrl: String) = userRepository.updateUserBackgroundPhotoUrl(userId, userBackgroundPhotoUrl)

    suspend fun updateGreetings(userId: String, greetings: String) = userRepository.updateGreetings(userId, greetings)
}