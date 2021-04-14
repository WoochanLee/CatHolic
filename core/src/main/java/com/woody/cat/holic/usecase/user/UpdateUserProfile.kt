package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository

class UpdateUserProfile(private val userRepository: UserRepository) {

    suspend fun updateDisplayName(userId: String, displayName: String) = userRepository.updateDisplayName(userId, displayName)

    suspend fun updateUserPhotoUrl(userId: String, userPhotoUrl: String) = userRepository.updateUserPhotoUrl(userId, userPhotoUrl)

    suspend fun updateBackgroundPhotoUrl(userId: String, userBackgroundPhotoUrl: String) = userRepository.updateBackgroundPhotoUrl(userId, userBackgroundPhotoUrl)

    suspend fun updateGreetings(userId: String, greetings: String) = userRepository.updateGreetings(userId, greetings)
}