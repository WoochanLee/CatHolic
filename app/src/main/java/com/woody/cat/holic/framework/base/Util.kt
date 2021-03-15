package com.woody.cat.holic.framework.base

import java.util.logging.Logger

object CatHolicLogger {
    fun log(msg: String) {
        Logger.getGlobal().info(msg)
    }
}