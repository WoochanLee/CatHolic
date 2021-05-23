package com.woody.cat.holic.framework.db

import android.content.SharedPreferences
import com.woody.cat.holic.data.SettingRepository

class SharedPreferenceSettingRepository(private val settingSharedPreferences: SharedPreferences) : SettingRepository {
    companion object {
        private const val KEY_DARK_MODE = "KEY_DARK_MODE"
        private const val KEY_MAIN_GUIDE = "KEY_MAIN_GUIDE"
        private const val KEY_UPLOAD_GUIDE = "KEY_UPLOAD_GUIDE"
        private const val KEY_LIKED_COUNT = "KEY_LIKED_COUNT"
        private const val KEY_HAVE_TO_SHOW_IN_APP_REVIEW = "KEY_HAVE_TO_SHOW_IN_APP_REVIEW"

        private const val LIKED_COUNT_FOR_IN_APP_REVIEW = 3
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

    override fun addLikedCountAndCheckInAppReviewCondition() {
        settingSharedPreferences.edit().apply {
            putInt(KEY_LIKED_COUNT, settingSharedPreferences.getInt(KEY_LIKED_COUNT, 0) + 1)
            apply()
        }

        if (settingSharedPreferences.getInt(KEY_LIKED_COUNT, 0) == LIKED_COUNT_FOR_IN_APP_REVIEW) {
            setHaveToShowInAppReview(true)
        }
    }

    override fun setHaveToShowInAppReview(haveToShowInAppReview: Boolean) {
        settingSharedPreferences.edit().apply {
            putBoolean(KEY_HAVE_TO_SHOW_IN_APP_REVIEW, haveToShowInAppReview)
            apply()
        }
    }

    override fun getHaveToShowInAppReview(): Boolean {
        return settingSharedPreferences.getBoolean(KEY_HAVE_TO_SHOW_IN_APP_REVIEW, false)
    }
}