package com.woody.cat.holic.usecase.setting

import com.woody.cat.holic.data.SettingRepository

class UpdateAppSetting(private val settingRepository: SettingRepository) {
    fun setDarkMode(isDarkMode: Boolean) = settingRepository.setDarkMode(isDarkMode)

    fun setMainGuideStatus(isVisible: Boolean) = settingRepository.setMainGuideStatus(isVisible)

    fun setUploadGuideStatus(isVisible: Boolean) = settingRepository.setUploadGuideStatus(isVisible)
}