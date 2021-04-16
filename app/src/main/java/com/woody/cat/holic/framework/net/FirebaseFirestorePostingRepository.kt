package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.data.common.PostingOrder
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.COLLECTION_PROFILE_PATH
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.net.dto.PostingDto
import com.woody.cat.holic.framework.net.dto.UserDto
import com.woody.cat.holic.framework.net.dto.mapToPosting
import com.woody.cat.holic.framework.net.dto.mapToPostingDto
import kotlinx.coroutines.CompletableDeferred


class FirebaseFirestorePostingRepository(private val db: FirebaseFirestore) : PostingRepository {

    override var currentGalleryPostingOrder = PostingOrder.LIKED
    override var currentLikePostingOrder = PostingOrder.CREATED

    override fun getPostingOrder(postingType: PostingType): PostingOrder {
        return when (postingType) {
            PostingType.GALLERY -> currentGalleryPostingOrder
            PostingType.LIKED -> currentLikePostingOrder
            PostingType.USER -> PostingOrder.LIKED
        }
    }

    override fun changeToNextPostingOrder(postingType: PostingType) {
        when (postingType) {
            PostingType.GALLERY -> currentGalleryPostingOrder = currentGalleryPostingOrder.getNextPostingOrder()
            PostingType.LIKED -> currentLikePostingOrder = currentLikePostingOrder.getNextPostingOrder()
            PostingType.USER -> Unit
        }
    }

    override suspend fun addPosting(userId: String, postings: List<Posting>): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(userId), UserDto::postingCount.name, FieldValue.increment(1))
            postings.forEach { posting ->
                it.set(db.collection(COLLECTION_POSTING_PATH).document(), posting.mapToPostingDto())
            }
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
            CatHolicLogger.log("success to add posting")
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
            CatHolicLogger.log("fail to add posting")
        }

        return dataDeferred.await()
    }

    override suspend fun removePosting(userId: String, postingId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(userId), UserDto::postingCount.name, FieldValue.increment(-1))
            it.update(db.collection(COLLECTION_POSTING_PATH).document(postingId), PostingDto::deleted.name, true)
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
        }

        return dataDeferred.await()
    }

    override suspend fun getGalleryPostings(key: String?, size: Int): Resource<List<Posting>> {

        val lastDoc = getPagingKey(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()

        db.collection(COLLECTION_POSTING_PATH)
            .whereEqualTo(PostingDto::deleted.name, false)
            .run {
                when (currentGalleryPostingOrder) {
                    PostingOrder.CREATED -> orderBy(PostingDto::created.name, Query.Direction.DESCENDING)
                    PostingOrder.LIKED -> orderBy(PostingDto::likeCount.name, Query.Direction.DESCENDING)
                    PostingOrder.RANDOM -> this
                }
            }.run {
                if (key != null && lastDoc.data != null) {
                    startAfter(lastDoc.data as DocumentSnapshot)
                } else this
            }.limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postingList = querySnapshot.documents.mapNotNull {
                    val postingDto = it.toObject(PostingDto::class.java)
                    postingDto?.mapToPosting(postingId = it.id)
                }

                dataDeferred.complete(Resource.Success(postingList))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    override suspend fun getUserLikePostings(key: String?, userId: String, size: Int): Resource<List<Posting>> {

        val lastDoc = getPagingKey(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()

        db.collection(COLLECTION_POSTING_PATH)
            .whereEqualTo(PostingDto::deleted.name, false)
            .whereArrayContains(PostingDto::likedUserIds.name, userId)
            .run {
                when (currentLikePostingOrder) {
                    PostingOrder.CREATED -> orderBy(PostingDto::created.name, Query.Direction.DESCENDING)
                    PostingOrder.LIKED -> orderBy(PostingDto::likeCount.name, Query.Direction.DESCENDING)
                    PostingOrder.RANDOM -> this
                }
            }.run {
                if (key != null && lastDoc.data != null) {
                    startAfter(lastDoc.data as DocumentSnapshot)
                } else this
            }.limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postingList = querySnapshot.documents.mapNotNull {
                    val postingDto = it.toObject(PostingDto::class.java)
                    postingDto?.mapToPosting(postingId = it.id)
                }

                dataDeferred.complete(Resource.Success(postingList))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    override suspend fun getUserUploadedPostings(key: String?, userId: String, size: Int): Resource<List<Posting>> {
        val lastDoc = getPagingKey(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()

        db.collection(COLLECTION_POSTING_PATH)
            .whereEqualTo(PostingDto::deleted.name, false)
            .whereEqualTo(PostingDto::userId.name, userId)
            .orderBy(PostingDto::created.name, Query.Direction.DESCENDING)
            .run {
                if (key != null && lastDoc.data != null) {
                    startAfter(lastDoc.data as DocumentSnapshot)
                } else this
            }.limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postingList = querySnapshot.documents.mapNotNull {
                    val postingDto = it.toObject(PostingDto::class.java)
                    postingDto?.mapToPosting(postingId = it.id)
                }

                dataDeferred.complete(Resource.Success(postingList))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    private suspend fun getPagingKey(key: String?): Resource<DocumentSnapshot?> {
        val lastDocDeferred = CompletableDeferred<Resource<DocumentSnapshot?>>()

        if (key != null) {
            db.collection(COLLECTION_POSTING_PATH)
                .document(key)
                .get()
                .addOnSuccessListener {
                    lastDocDeferred.complete(Resource.Success(it))
                }.addOnFailureListener {
                    lastDocDeferred.complete(Resource.Error(it))
                }
        } else {
            lastDocDeferred.complete(Resource.Success(null))
        }

        return lastDocDeferred.await()
    }
}