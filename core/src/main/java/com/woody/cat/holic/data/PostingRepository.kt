package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting

interface PostingRepository {

    suspend fun addPosting(postings: List<Posting>): Resource<Unit>

    suspend fun getNextNormalPostings(fromTheTop: Boolean, size: Int, orderBy: PostingOrder): Resource<List<Posting>>

    suspend fun getNextLikedPostings(fromTheTop: Boolean, size: Int, orderBy: PostingOrder): Resource<List<Posting>>

    suspend fun addLikedInPosting(userId: String, postingId: String): Resource<Unit>

    suspend fun removeLikedInPosting(userId: String, postingId: String): Resource<Unit>
}

enum class PostingOrder(val fieldName: String) {
    CREATED("created"),
    LIKED("liked"),
    RANDOM("random") //TODO: need more logic. (randomly combine order, value, etc..)
}