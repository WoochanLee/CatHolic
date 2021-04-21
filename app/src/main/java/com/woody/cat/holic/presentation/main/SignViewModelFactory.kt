package com.woody.cat.holic.presentation.main

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.setting.GetPushToken
import com.woody.cat.holic.usecase.user.*

class SignViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignViewModel::class.java)) {
            return SignViewModel(
                refreshEventBus,
                FirebaseAuth.getInstance(),
                GetIsSignedIn(userRepository),
                GetCurrentUserId(userRepository),
                GetUserProfile(userRepository),
                AddUserProfile(userRepository),
                GetPushToken(pushTokenGenerateRepository),
                AddPushToken(pushTokenRepository),
                RemovePushToken(pushTokenRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}