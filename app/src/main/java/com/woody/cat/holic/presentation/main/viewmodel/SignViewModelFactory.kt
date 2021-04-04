package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.user.AddUserProfile
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import com.woody.cat.holic.usecase.user.GetUserProfile

class SignViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignViewModel::class.java)) {
            return SignViewModel(
                FirebaseAuth.getInstance(),
                GetIsSignedIn(CatHolicApplication.application.userRepository),
                GetCurrentUserId(CatHolicApplication.application.userRepository),
                GetUserProfile(CatHolicApplication.application.userRepository),
                AddUserProfile(CatHolicApplication.application.userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}