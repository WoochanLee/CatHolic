package com.woody.cat.holic.usecase.config

import com.woody.cat.holic.data.RemoteConfigRepository

class RefreshRemoteConfig(private val remoteConfigRepository: RemoteConfigRepository) {

    suspend operator fun invoke() = remoteConfigRepository.refreshRemoteConfig()
}