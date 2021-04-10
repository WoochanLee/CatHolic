package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.user.AddUserProfile
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import com.woody.cat.holic.usecase.user.GetUserProfile

class SignViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignViewModel::class.java)) {
            return SignViewModel(
                FirebaseAuth.getInstance(),
                GetIsSignedIn(userRepository),
                GetCurrentUserId(userRepository),
                GetUserProfile(userRepository),
                AddUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}