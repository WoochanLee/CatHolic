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
import com.woody.cat.holic.framework.net.common.DataNotExistException
import com.woody.cat.holic.framework.net.dto.PostingDto
import com.woody.cat.holic.framework.net.dto.UserDto
import com.woody.cat.holic.framework.net.dto.mapToPosting
import com.woody.cat.holic.framework.net.dto.mapToPostingDto
import kotlinx.coroutines.tasks.await


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
        return try {
            db.runTransaction { transaction ->
                transaction.update(
                    db.collection(COLLECTION_PROFILE_PATH).document(userId),
                    UserDto::postingCount.name,
                    FieldValue.increment(postings.size.toLong())
                )
                postings.forEach { posting ->
                    transaction.set(db.collection(COLLECTION_POSTING_PATH).document(), posting.mapToPostingDto())
                }
            }.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun removePosting(userId: String, postingId: String): Resource<Unit> {
        return try {
            db.runTransaction {
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(userId), UserDto::postingCount.name, FieldValue.increment(-1))
                it.update(db.collection(COLLECTION_POSTING_PATH).document(postingId), PostingDto::deleted.name, true)
            }.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getGalleryPostings(key: String?, size: Int): Resource<List<Posting>> {

        val lastDoc = getPagingKeyDoc(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        return try {
            val querySnapshot = db.collection(COLLECTION_POSTING_PATH)
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
                .await()

            val postingList = querySnapshot.documents.mapNotNull {
                val postingDto = it.toObject(PostingDto::class.java)
                postingDto?.mapToPosting(postingId = it.id)
            }

            Resource.Success(postingList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getUserLikePostings(key: String?, userId: String, size: Int): Resource<List<Posting>> {

        val lastDoc = getPagingKeyDoc(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        return try {
            val querySnapshot = db.collection(COLLECTION_POSTING_PATH)
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
                .await()

            val postingList = querySnapshot.documents.mapNotNull {
                val postingDto = it.toObject(PostingDto::class.java)
                postingDto?.mapToPosting(postingId = it.id)
            }

            Resource.Success(postingList)
        }catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getUserUploadedPostings(key: String?, userId: String, size: Int): Resource<List<Posting>> {
        val lastDoc = getPagingKeyDoc(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        return try {
            val querySnapshot = db.collection(COLLECTION_POSTING_PATH)
                .whereEqualTo(PostingDto::deleted.name, false)
                .whereEqualTo(PostingDto::userId.name, userId)
                .orderBy(PostingDto::created.name, Query.Direction.DESCENDING)
                .run {
                    if (key != null && lastDoc.data != null) {
                        startAfter(lastDoc.data as DocumentSnapshot)
                    } else this
                }.limit(size.toLong())
                .get()
                .await()

            val postingList = querySnapshot.documents.mapNotNull {
                val postingDto = it.toObject(PostingDto::class.java)
                postingDto?.mapToPosting(postingId = it.id)
            }

            Resource.Success(postingList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getUserPostings(key: String?, userId: String, size: Int): Resource<List<Posting>> {
        val lastDoc = getPagingKeyDoc(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        return try {
            val querySnapshot = db.collection(COLLECTION_POSTING_PATH)
                .whereEqualTo(PostingDto::deleted.name, false)
                .whereEqualTo(PostingDto::userId.name, userId)
                .orderBy(PostingDto::created.name, Query.Direction.DESCENDING)
                .run {
                    if (key != null && lastDoc.data != null) {
                        startAfter(lastDoc.data as DocumentSnapshot)
                    } else this
                }.limit(size.toLong())
                .get()
                .await()

            val postingList = querySnapshot.documents.mapNotNull {
                val postingDto = it.toObject(PostingDto::class.java)
                postingDto?.mapToPosting(postingId = it.id)
            }

            Resource.Success(postingList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getSinglePosting(postingId: String): Resource<Posting> {
        return try {
            val result = db.collection(COLLECTION_POSTING_PATH)
                .document(postingId)
                .get()
                .await()

            val user = result.toObject(PostingDto::class.java)?.mapToPosting(postingId)
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Error(DataNotExistException())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getPagingKeyDoc(key: String?): Resource<DocumentSnapshot?> {
        if (key == null) return Resource.Success(null)

        return try {
            val result = db.collection(COLLECTION_POSTING_PATH)
                .document(key)
                .get()
                .await()

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}