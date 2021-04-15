package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.setting.UpdateAppSetting

class UserViewModelFactory : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(
                GetAppSetting(settingRepository),
                UpdateAppSetting(settingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}