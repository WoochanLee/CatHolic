package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.user.FirebaseUserManager

class SignViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignViewModel::class.java)) {
            return SignViewModel(FirebaseUserManager) as T
        } else {
            throw IllegalStateException()
        }
    }
}