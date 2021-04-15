package com.woody.cat.holic.usecase.setting

import com.woody.cat.holic.data.SettingRepository

class GetAppSetting(private val settingRepository: SettingRepository) {
    fun getDarkMode() = settingRepository.getDarkMode()
}