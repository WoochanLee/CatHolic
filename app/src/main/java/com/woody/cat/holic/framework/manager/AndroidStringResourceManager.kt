package com.woody.cat.holic.framework.manager

import android.content.Context
import androidx.annotation.StringRes

class AndroidStringResourceManager(private val context: Context) {

    fun getString(@StringRes stringRes: Int): String {
        return context.getString(stringRes)
    }

    fun getString(@StringRes stringRes: Int, argStr: String): String {
        return context.getString(stringRes, argStr)
    }
}