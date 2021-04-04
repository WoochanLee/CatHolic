package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository

class GetCurrentUserId(private val userRepository: UserRepository) {

    operator fun invoke() = userRepository.getCurrentUserId()
}