package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.ReportPostingRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.net.dto.PostingDto
import kotlinx.coroutines.tasks.await

class FirebaseReportPostingRepository(private val db: FirebaseFirestore): ReportPostingRepository {
    override suspend fun reportPosting(userId: String, postingId: String): Resource<Unit> {
        return try {
            db.runTransaction { transaction ->

                val postingDto = transaction.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

                val isUserAlreadyReported = postingDto?.reportedUserIds?.contains(userId) == true

                if (!isUserAlreadyReported) {
                    transaction.update(db.collection(COLLECTION_POSTING_PATH).document(postingId), PostingDto::reportCount.name, FieldValue.increment(1))
                    transaction.update(db.collection(COLLECTION_POSTING_PATH).document(postingId), PostingDto::reportedUserIds.name, FieldValue.arrayUnion(userId))
                }
            }.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}