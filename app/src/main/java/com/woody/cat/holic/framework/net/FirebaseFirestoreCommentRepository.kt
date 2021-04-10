package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.woody.cat.holic.data.CommentRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Comment
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.COLLECTION_COMMENT_PATH
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.net.dto.CommentDto
import com.woody.cat.holic.framework.net.dto.PostingDto
import com.woody.cat.holic.framework.net.dto.mapToComment
import com.woody.cat.holic.framework.net.dto.mapToCommentDto
import kotlinx.coroutines.CompletableDeferred

class FirebaseFirestoreCommentRepository(private val db: FirebaseFirestore) : CommentRepository {

    override suspend fun addComment(comment: Comment): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        val commentDto = comment.mapToCommentDto()

        db.runTransaction {
            val commentDocumentReference = db.collection(COLLECTION_COMMENT_PATH).document()
            val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(comment.postingId)
            val postingDto = it.get(postingDocumentReference).toObject(PostingDto::class.java)

            it.set(commentDocumentReference, commentDto)
            it.update(postingDocumentReference, Posting::commentIds.name, FieldValue.arrayUnion(comment.userId))
            it.update(postingDocumentReference, Posting::commented.name, (postingDto?.commentIds?.size ?: 0) + 1)
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
            CatHolicLogger.log("success to add comment to posting")
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
            CatHolicLogger.log("fail to add comment to posting")
        }

        return dataDeferred.await()
    }

    override suspend fun getComments(key: String?, postingId: String): Resource<List<Comment>> {
        val lastDoc = getPagingKey(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        val dataDeferred = CompletableDeferred<Resource<List<Comment>>>()

        db.collection(COLLECTION_COMMENT_PATH)
            .whereEqualTo(Comment::postingId.name, postingId)
            .orderBy(Comment::created.name, Query.Direction.DESCENDING)
            .run {
                if (key != null && lastDoc.data != null) {
                    startAfter(lastDoc.data as DocumentSnapshot)
                } else this
            }
            .get()
            .addOnSuccessListener { querySnapshot ->
                val commentList = querySnapshot.documents.mapNotNull {
                    val commentDto = it.toObject(CommentDto::class.java)
                    commentDto?.mapToComment(commentId = it.id)
                }

                dataDeferred.complete(Resource.Success(commentList))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    private suspend fun getPagingKey(key: String?): Resource<DocumentSnapshot?> {
        val lastDocDeferred = CompletableDeferred<Resource<DocumentSnapshot?>>()

        if (key != null) {
            db.collection(COLLECTION_COMMENT_PATH)
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