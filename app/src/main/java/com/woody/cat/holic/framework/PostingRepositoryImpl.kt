package com.woody.cat.holic.framework

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.net.PostingDto
import com.woody.cat.holic.framework.net.mapToPosting
import com.woody.cat.holic.framework.net.mapToPostingDto
import kotlinx.coroutines.CompletableDeferred


class PostingRepositoryImpl(private val firebaseUserManager: FirebaseUserManager) : PostingRepository {

    companion object {
        const val COLLECTION_PATH = "cat"
    }

    private val db = FirebaseFirestore.getInstance()

    override suspend fun addPosting(postings: List<Posting>): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        val batch = db.batch()
        postings.forEach {
            batch.set(db.collection(COLLECTION_PATH).document(), it.mapToPostingDto())
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

    private var lastVisibleDocument: DocumentSnapshot? = null

    override suspend fun getNextPostings(
        size: Int,
        orderBy: PostingOrder
    ): Resource<List<Posting>> {
        val dataDeferred = CompletableDeferred<Resource<List<Posting>>>()

        db.collection(COLLECTION_PATH)
            .orderBy(orderBy.fieldName)
            .apply {
                if (lastVisibleDocument != null) {
                    this.startAfter(lastVisibleDocument)
                }
            }
            .limit(size.toLong())
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (querySnapshot.size() != 0) {
                    lastVisibleDocument = querySnapshot.documents[querySnapshot.size() - 1]
                }

                val postingList = querySnapshot.documents
                    .mapNotNull { it.toObject(PostingDto::class.java) }
                    .map { it.mapToPosting() }

                dataDeferred.complete(Resource.Success(postingList))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }
}