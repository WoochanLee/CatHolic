package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.PostingOrder
import com.woody.cat.holic.data.common.PostingType
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
}