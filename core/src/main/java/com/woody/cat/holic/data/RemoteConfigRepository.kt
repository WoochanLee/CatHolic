package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface RemoteConfigRepository {

    companion object {
        const val KEY_FORCE_UPDATE_VERSION = "KEY_FORCE_UPDATE_VERSION"
        const val KEY_IS_SERVICE_AVAILABLE = "KEY_IS_SERVICE_AVAILABLE"
    }

    suspend fun refreshRemoteConfig(): Resource<Unit>

    fun getForceUpdateVersion(): Long

    fun getIsServiceAvailable(): Boolean
}