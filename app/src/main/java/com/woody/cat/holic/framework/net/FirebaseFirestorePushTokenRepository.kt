package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.PushTokenRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.COLLECTION_PUSH_TOKEN_PATH
import com.woody.cat.holic.framework.net.dto.PushTokenDto
import kotlinx.coroutines.tasks.await

class FirebaseFirestorePushTokenRepository(private val db: FirebaseFirestore) : PushTokenRepository {
    override suspend fun addPushToken(userId: String, token: String): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PUSH_TOKEN_PATH)
                .document(userId)
                .set(PushTokenDto(token))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun removePushToken(userId: String): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PUSH_TOKEN_PATH)
                .document(userId)
                .delete()
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}