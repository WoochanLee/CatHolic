package com.woody.cat.holic.usecase.user

import com.woody.cat.holic.data.UserRepository
import com.woody.cat.holic.domain.User

class AddUserProfile(private val userRepository: UserRepository)  {

    suspend operator fun invoke(user: User) = userRepository.addUserProfile(user)
}