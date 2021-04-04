package com.woody.cat.holic.framework.db

import android.content.SharedPreferences
import com.woody.cat.holic.data.SettingRepository

class SettingRepositoryImpl(private val settingSharedPreferences: SharedPreferences) : SettingRepository {
    companion object {
        private const val KEY_DARK_MODE = "DARK_MODE"
    }

    override fun setDarkMode(isDarkMode: Boolean) {
        settingSharedPreferences.edit().apply {
            putBoolean(KEY_DARK_MODE, isDarkMode)
            apply()
        }
    }

    override fun getDarkMode(): Boolean {
        return settingSharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }
}