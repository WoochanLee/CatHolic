package com.woody.cat.holic.framework.net

import com.google.firebase.messaging.FirebaseMessaging
import com.woody.cat.holic.data.PushTokenGenerateRepository
import kotlinx.coroutines.tasks.await

class FirebaseMessagingPushTokenGenerateRepository(
    private val fm: FirebaseMessaging,

    ): PushTokenGenerateRepository {

    override suspend fun generatePushToken(): String? {
        return try {
            fm.token.await()
        }catch (e: Exception) {
            null
        }
    }
}