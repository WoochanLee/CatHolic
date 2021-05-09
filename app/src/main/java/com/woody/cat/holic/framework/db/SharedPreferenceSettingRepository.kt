package com.woody.cat.holic.framework.db

import android.content.SharedPreferences
import com.woody.cat.holic.data.SettingRepository

class SharedPreferenceSettingRepository(private val settingSharedPreferences: SharedPreferences) : SettingRepository {
    companion object {
        private const val KEY_DARK_MODE = "DARK_MODE"
        private const val KEY_MAIN_GUIDE = "MAIN_GUIDE"
        private const val KEY_UPLOAD_GUIDE = "UPLOAD_GUIDE"
    }

    override fun setDarkMode(isDarkMode: Boolean) {
        settingSharedPreferences.edit().apply {
            putBoolean(KEY_DARK_MODE, isDarkMode)
            apply()
        }
    }

    override fun getDarkMode(): Boolean {
        return settingSharedPreferences.getBoolean(KEY_DARK_MODE, true)
    }

    override fun setMainGuideStatus(isVisible: Boolean) {
        settingSharedPreferences.edit().apply {
            putBoolean(KEY_MAIN_GUIDE, isVisible)
            apply()
        }
    }

    override fun getMainGuideStatus(): Boolean {
        return settingSharedPreferences.getBoolean(KEY_MAIN_GUIDE, true)
    }

    override fun setUploadGuideStatus(isVisible: Boolean) {
        settingSharedPreferences.edit().apply {
            putBoolean(KEY_UPLOAD_GUIDE, isVisible)
            apply()
        }
    }

    override fun getUploadGuideStatus(): Boolean {
        return settingSharedPreferences.getBoolean(KEY_UPLOAD_GUIDE, true)
    }
}