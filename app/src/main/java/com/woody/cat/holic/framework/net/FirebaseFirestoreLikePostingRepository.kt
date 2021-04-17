package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.LikePostingRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.net.dto.PostingDto
import kotlinx.coroutines.tasks.await


class FirebaseFirestoreLikePostingRepository(private val db: FirebaseFirestore) : LikePostingRepository {

    override suspend fun likePosting(userId: String, postingId: String): Resource<Unit> {
        return try {
            db.runTransaction {
                val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

                val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

                if (isUserAlreadyLiked) {
                    throw IllegalStateException()
                }

                val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(postingId)

                it.update(postingDocumentReference, Posting::likedUserIds.name, FieldValue.arrayUnion(userId))
                it.update(postingDocumentReference, Posting::likeCount.name, FieldValue.increment(1))
            }.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun unlikePosting(userId: String, postingId: String): Resource<Unit> {
        return try {
            db.runTransaction {
                val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

                val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

                if (!isUserAlreadyLiked) {
                    throw IllegalStateException()
                }

                val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(postingId)

                it.update(postingDocumentReference, Posting::likedUserIds.name, FieldValue.arrayRemove(userId))
                it.update(postingDocumentReference, Posting::likeCount.name, FieldValue.increment(-1))
            }.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}