package com.woody.cat.holic.presentation.main.like.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.user.FirebaseUserManager

class LikeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeViewModel::class.java)) {
            return LikeViewModel(FirebaseUserManager, CatHolicApplication.application.postingRepository) as T
        } else {
            throw IllegalStateException()
        }
    }
}