package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository

class GetIsSignedIn(private val userRepository: UserRepository) {

    operator fun invoke() = userRepository.isSignedIn()
}