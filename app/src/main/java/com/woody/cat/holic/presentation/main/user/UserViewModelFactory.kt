package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.FirebaseUserManager

class UserViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(FirebaseUserManager) as T
        } else {
            throw IllegalStateException()
        }
    }
}