package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting

interface PostingRepository {

    var currentGalleryPostingOrder: PostingOrder
    var currentLikePostingOrder: PostingOrder

    suspend fun addPosting(postings: List<Posting>): Resource<Unit>

    suspend fun getGalleryPostings(key: String?, size: Int, isChangeToNextOrder: Boolean): Resource<List<Posting>>

    suspend fun getUserLikePostings(key: String?, userId: String, size: Int, isChangeToNextOrder: Boolean): Resource<List<Posting>>

    suspend fun addLikedInPosting(userId: String, postingId: String): Resource<Unit>

    suspend fun removeLikedInPosting(userId: String, postingId: String): Resource<Unit>
}

enum class PostingOrder(val fieldName: String) {
    CREATED("created"),
    LIKED("liked"),
    RANDOM("random"); //TODO: need more logic. (randomly combine order, value, etc..)

    fun getNextPostingOrder(): PostingOrder {
        return when (this) {
            LIKED -> CREATED
            CREATED -> RANDOM
            RANDOM -> LIKED
        }
    }
}