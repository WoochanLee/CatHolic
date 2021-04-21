package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface PushTokenRepository {

    suspend fun addPushToken(userId: String, token: String): Resource<Unit>

    suspend fun removePushToken(userId: String): Resource<Unit>
}