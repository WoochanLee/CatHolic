package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository

class GetLikePostings(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(key: String?, size: Int, orderBy: PostingOrder) = postingRepository.getLikePostings(key, size, orderBy)
}