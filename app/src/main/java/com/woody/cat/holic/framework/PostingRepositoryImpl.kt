package com.woody.cat.holic.framework

import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.CatHolicLogger


class PostingRepositoryImpl : PostingRepository {

    companion object {
        const val COLLECTION_PATH = "cat"
    }

    private val db = FirebaseFirestore.getInstance()

    override suspend fun addPostings(postings: List<Posting>) {
        val batch = db.batch()
        postings.forEach {
            batch.set(db.collection(COLLECTION_PATH).document(), it)
        }
        batch.commit().addOnSuccessListener {
            CatHolicLogger.log("success to add posting")
        }.addOnFailureListener {
            CatHolicLogger.log("fail to add posting")
        }
    }
}