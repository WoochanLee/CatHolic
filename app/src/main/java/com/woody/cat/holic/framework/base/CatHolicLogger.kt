package com.woody.cat.holic.framework.base

import com.woody.cat.holic.BuildConfig
import java.util.logging.Logger

object CatHolicLogger {
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Logger.getGlobal().info(msg)
        }
    }
}