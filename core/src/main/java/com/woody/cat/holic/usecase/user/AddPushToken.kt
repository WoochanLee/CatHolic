package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.PushTokenRepository

class AddPushToken(private val pushTokenRepository: PushTokenRepository) {

    suspend operator fun invoke(userId: String, token: String) = pushTokenRepository.addPushToken(userId, token)
}