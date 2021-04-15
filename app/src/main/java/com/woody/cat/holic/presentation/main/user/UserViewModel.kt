package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.BuildConfig
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.setting.UpdateAppSetting

class UserViewModel(
    private val getAppSetting: GetAppSetting,
    private val updateAppSetting: UpdateAppSetting
) : BaseViewModel() {

    val versionName = BuildConfig.VERSION_NAME

    private val _isDarkMode = MutableLiveData(getAppSetting.getDarkMode())
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    private val _eventChangeDarkMode = MutableLiveData<Event<Boolean>>()
    val eventChangeDarkMode: LiveData<Event<Boolean>> get() = _eventChangeDarkMode

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    fun changeDarkMode() {
        val changedDarkMode = isDarkMode.value != true
        updateAppSetting.setDarkMode(changedDarkMode)
        _isDarkMode.postValue(changedDarkMode)

        _eventChangeDarkMode.emit(changedDarkMode)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }
}