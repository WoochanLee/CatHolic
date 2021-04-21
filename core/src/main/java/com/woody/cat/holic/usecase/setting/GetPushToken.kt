package com.woody.cat.holic.usecase.setting

import com.woody.cat.holic.data.PushTokenGenerateRepository

class GetPushToken(private val pushTokenGenerateRepository: PushTokenGenerateRepository) {

    suspend operator fun invoke() = pushTokenGenerateRepository.generatePushToken()
}