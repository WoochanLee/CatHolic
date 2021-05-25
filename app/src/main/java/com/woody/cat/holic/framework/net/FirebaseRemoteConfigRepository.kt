package com.woody.cat.holic.framework.net

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.woody.cat.holic.data.RemoteConfigRepository
import com.woody.cat.holic.data.common.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteConfigRepository @Inject constructor(private val firebaseRemoteConfig: FirebaseRemoteConfig) : RemoteConfigRepository {
    override suspend fun refreshRemoteConfig(): Resource<Unit> {
        return try {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60 * 12)
                .build()

            firebaseRemoteConfig.apply {
                setConfigSettingsAsync(configSettings)
                fetchAndActivate().await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getForceUpdateVersion(): Long {
        return firebaseRemoteConfig.getLong(RemoteConfigRepository.KEY_FORCE_UPDATE_VERSION)
    }

    override fun getIsServiceAvailable(): Boolean {
        return firebaseRemoteConfig.getBoolean(RemoteConfigRepository.KEY_IS_SERVICE_AVAILABLE)
    }
}