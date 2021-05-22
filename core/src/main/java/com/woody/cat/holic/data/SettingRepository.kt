package com.woody.cat.holic.data

interface SettingRepository {

    fun setDarkMode(isDarkMode: Boolean)

    fun getDarkMode(): Boolean

    fun setMainGuideStatus(isVisible: Boolean)

    fun getMainGuideStatus(): Boolean

    fun setUploadGuideStatus(isVisible: Boolean)

    fun getUploadGuideStatus(): Boolean
}
