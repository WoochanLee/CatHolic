package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory

class UserViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(settingRepository) as T
        } else {
            throw IllegalStateException()
        }
    }
}