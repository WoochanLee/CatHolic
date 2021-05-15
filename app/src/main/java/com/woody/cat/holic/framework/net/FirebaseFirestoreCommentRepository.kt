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
import com.woody.cat.holic.framework.net.dto.CommentDto
import com.woody.cat.holic.framework.net.dto.PostingDto
import com.woody.cat.holic.framework.net.dto.mapToComment
import com.woody.cat.holic.framework.net.dto.mapToCommentDto
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreCommentRepository(private val db: FirebaseFirestore) : CommentRepository {

    override suspend fun addComment(comment: Comment): Resource<Unit> {
        val commentDto = comment.mapToCommentDto()

        return try {
            db.runTransaction {
                val commentDocumentReference = db.collection(COLLECTION_COMMENT_PATH).document()
                val postingDocumentReference = db.collection(COLLECTION_POSTING_PATH).document(comment.postingId)
                val postingDto = it.get(postingDocumentReference).toObject(PostingDto::class.java)

                it.set(commentDocumentReference, commentDto)
                it.update(postingDocumentReference, Posting::commentIds.name, FieldValue.arrayUnion(commentDocumentReference.id))
                it.update(postingDocumentReference, Posting::commentUserIds.name, FieldValue.arrayUnion(comment.userId))
                it.update(postingDocumentReference, Posting::commentCount.name, (postingDto?.commentIds?.size ?: 0) + 1)
            }.await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getComments(key: String?, postingId: String, size: Int): Resource<List<Comment>> {
        val lastDoc = getPagingKeyDoc(key)

        if (lastDoc !is Resource.Success) {
            return Resource.Error(IllegalStateException())
        }

        return try {
            val querySnapshot = db.collection(COLLECTION_COMMENT_PATH)
                .whereEqualTo(Comment::postingId.name, postingId)
                .orderBy(Comment::created.name, Query.Direction.DESCENDING)
                .run {
                    if (key != null && lastDoc.data != null) {
                        startAfter(lastDoc.data as DocumentSnapshot)
                    } else this
                }.limit(size.toLong())
                .get()
                .await()

            val commentList = querySnapshot.documents.mapNotNull {
                val commentDto = it.toObject(CommentDto::class.java)
                commentDto?.mapToComment(commentId = it.id)
            }
            Resource.Success(commentList)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private suspend fun getPagingKeyDoc(key: String?): Resource<DocumentSnapshot?> {
        if (key == null) return Resource.Success(null)

        return try {
            val result = db.collection(COLLECTION_COMMENT_PATH)
                .document(key)
                .get()
                .await()

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}