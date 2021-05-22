package com.woody.cat.holic.usecase.config

import com.woody.cat.holic.data.RemoteConfigRepository

class GetIsServiceAvailable(private val remoteConfigRepository: RemoteConfigRepository) {

    operator fun invoke() = remoteConfigRepository.getIsServiceAvailable()
}