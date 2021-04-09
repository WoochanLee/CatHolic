package com.woody.cat.holic.presentation.main.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.data.SettingRepository
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit

class UserViewModel(private val settingRepository: SettingRepository): BaseViewModel() {

    private val _isDarkMode = MutableLiveData(settingRepository.getDarkMode())
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    private val _eventChangeDarkMode = MutableLiveData<Event<Boolean>>()
    val eventChangeDarkMode: LiveData<Event<Boolean>> get() = _eventChangeDarkMode

    fun changeDarkMode() {
        val changedDarkMode = isDarkMode.value != true
        settingRepository.setDarkMode(changedDarkMode)
        _isDarkMode.postValue(changedDarkMode)

        _eventChangeDarkMode.emit(changedDarkMode)
    }
}