package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.LikePostingRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.net.dto.PostingDto
import kotlinx.coroutines.CompletableDeferred


class FirebaseFirestoreLikePostingRepository(private val db: FirebaseFirestore) : LikePostingRepository {

    override suspend fun likePosting(userId: String, postingId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

            val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

            if (isUserAlreadyLiked) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
            }

            val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(postingId)

            it.update(postingDocumentReference, Posting::likedUserIds.name, FieldValue.arrayUnion(userId))
            it.update(postingDocumentReference, Posting::likeCount.name, FieldValue.increment(1))
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
            CatHolicLogger.log("success to add liked to posting")
        }.addOnFailureListener {
            it.printStackTrace()
            dataDeferred.complete(Resource.Error(it))
            CatHolicLogger.log("fail to add liked to posting")
        }

        return dataDeferred.await()
    }

    override suspend fun unlikePosting(userId: String, postingId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

            val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

            if (!isUserAlreadyLiked) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
            }

            val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(postingId)

            it.update(postingDocumentReference, Posting::likedUserIds.name, FieldValue.arrayRemove(userId))
            it.update(postingDocumentReference, Posting::likeCount.name, FieldValue.increment(-1))
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
            CatHolicLogger.log("success to add liked to posting")
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
            CatHolicLogger.log("fail to add liked to posting")
        }

        return dataDeferred.await()
    }
}