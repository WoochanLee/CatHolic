package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting

interface PostingRepository {

    var currentGalleryPostingOrder: PostingOrder
    var currentLikePostingOrder: PostingOrder

    fun getPostingOrder(postingType: PostingType): PostingOrder

    fun changeToNextPostingOrder(postingType: PostingType)

    suspend fun addPosting(userId: String, postings: List<Posting>): Resource<Unit>

    suspend fun removePosting(userId: String, postingId: String): Resource<Unit>

    suspend fun getGalleryPostings(key: String?, size: Int): Resource<List<Posting>>

    suspend fun getUserLikePostings(key: String?, userId: String, size: Int): Resource<List<Posting>>

    suspend fun getUserUploadedPostings(key: String?, userId: String, size: Int): Resource<List<Posting>>

    suspend fun addLikedInPosting(userId: String, postingId: String): Resource<Unit>

    suspend fun removeLikedInPosting(userId: String, postingId: String): Resource<Unit>
}

enum class PostingType {
    GALLERY,
    LIKED,
    USER
}

enum class PostingOrder {
    CREATED,
    LIKED,
    RANDOM; //TODO: need more logic. (randomly combine order, value, etc..)

    fun getNextPostingOrder(): PostingOrder {
        return when (this) {
            LIKED -> CREATED
            CREATED -> RANDOM
            RANDOM -> LIKED
        }
    }
}