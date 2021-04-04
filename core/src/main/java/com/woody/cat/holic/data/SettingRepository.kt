package com.woody.cat.holic.data

interface SettingRepository {

    //TODO: suspend
    fun setDarkMode(isDarkMode: Boolean)

    fun getDarkMode(): Boolean
}