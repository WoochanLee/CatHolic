package com.woody.cat.holic.framework

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.framework.net.LikeDto
import com.woody.cat.holic.framework.net.PostingDto
import com.woody.cat.holic.framework.net.mapToPosting
import com.woody.cat.holic.framework.net.mapToPostingDto
import kotlinx.coroutines.CompletableDeferred
import java.lang.Error


class PostingRepositoryImpl : PostingRepository {

    companion object {
        const val COLLECTION_POSTING_PATH = "posting"
        const val COLLECTION_LIKED_PATH = "liked"
    }

    private val db = FirebaseFirestore.getInstance()

    override suspend fun addPosting(postings: List<Posting>): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        val batch = db.batch()
        postings.forEach {
            batch.set(db.collection(COLLECTION_POSTING_PATH).document(), it.mapToPostingDto())
        }
        batch.commit().addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
            CatHolicLogger.log("success to add posting")
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
            CatHolicLogger.log("fail to add posting")
        }

        return dataDeferred.await()
    }

    private var lastDocInNormalPostings: DocumentSnapshot? = null

    override suspend fun getNextNormalPostings(
        fromTheTop: Boolean,
        size: Int,
        orderBy: PostingOrder
    ): Resource<List<Posting>> {

        if (fromTheTop) {
            lastDocInNormalPostings = null
        }

        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()

        db.collection(COLLECTION_POSTING_PATH)
            .run {
                when (orderBy) {
                    PostingOrder.CREATED,
                    PostingOrder.LIKED -> orderBy(orderBy.fieldName, Query.Direction.DESCENDING)
                    PostingOrder.RANDOM -> this
                }
            }.run {
                if (lastDocInNormalPostings != null) {
                    startAfter(lastDocInNormalPostings)
                } else this
            }.limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!dataDeferred.isCancelled && querySnapshot.size() != 0) {
                    lastDocInNormalPostings = querySnapshot.documents[querySnapshot.size() - 1]
                }

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

    override suspend fun getNextLikedPostings(fromTheTop: Boolean, size: Int, orderBy: PostingOrder): Resource<List<Posting>> {
        if (fromTheTop) {
            lastDocInNormalPostings = null
        }

        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()


        db.collection(COLLECTION_POSTING_PATH)
            .run {
                when (orderBy) {
                    PostingOrder.CREATED,
                    PostingOrder.LIKED -> orderBy(orderBy.fieldName, Query.Direction.DESCENDING)
                    PostingOrder.RANDOM -> this
                }
            }.run {
                if (lastDocInNormalPostings != null) {
                    startAfter(lastDocInNormalPostings)
                } else this
            }.limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!dataDeferred.isCancelled && querySnapshot.size() != 0) {
                    lastDocInNormalPostings = querySnapshot.documents[querySnapshot.size() - 1]
                }

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

    override suspend fun addLikedInPosting(userId: String, postingId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)
            val likeDto = it.get(db.collection(COLLECTION_LIKED_PATH).document(userId)).toObject(LikeDto::class.java)

            val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

            if (isUserAlreadyLiked) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
            }

            it.update(
                db.collection(COLLECTION_POSTING_PATH)
                    .document(postingId),
                Posting::likedUserIds.name, FieldValue.arrayUnion(userId)
            )

            it.update(
                db.collection(COLLECTION_POSTING_PATH)
                    .document(postingId),
                Posting::liked.name, (postingDto?.likedUserIds?.size ?: 0) + 1
            )

            if(likeDto == null) {
                it.set(
                    db.collection(COLLECTION_LIKED_PATH)
                        .document(userId),
                    LikeDto()
                )
            }

            it.update(
                db.collection(COLLECTION_LIKED_PATH)
                    .document(userId),
                LikeDto::likedPostingIds.name, FieldValue.arrayUnion(postingId)
            )
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

    override suspend fun removeLikedInPosting(userId: String, postingId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            val postingDto = it.get(db.collection(COLLECTION_POSTING_PATH).document(postingId)).toObject(PostingDto::class.java)

            val isUserAlreadyLiked = postingDto?.likedUserIds?.contains(userId) == true

            if (!isUserAlreadyLiked) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
            }

            it.update(
                db.collection(COLLECTION_POSTING_PATH)
                    .document(postingId),
                Posting::likedUserIds.name, FieldValue.arrayRemove(userId)
            )

            it.update(
                db.collection(COLLECTION_POSTING_PATH)
                    .document(postingId),
                Posting::liked.name, (postingDto?.likedUserIds?.size ?: 0) - 1
            )

            it.update(
                db.collection(COLLECTION_LIKED_PATH)
                    .document(userId),
                LikeDto::likedPostingIds.name, FieldValue.arrayRemove(postingId)
            )
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