package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.PushTokenRepository

class RemovePushToken(private val pushTokenRepository: PushTokenRepository) {

    suspend operator fun invoke(userId: String) = pushTokenRepository.removePushToken(userId)
}