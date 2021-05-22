package com.woody.cat.holic.usecase.config

import com.woody.cat.holic.data.RemoteConfigRepository

class GetForceUpdateVersion(private val remoteConfigRepository: RemoteConfigRepository) {

    operator fun invoke() = remoteConfigRepository.getForceUpdateVersion()
}