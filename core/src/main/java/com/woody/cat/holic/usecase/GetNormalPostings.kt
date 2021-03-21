package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository

class GetNormalPostings(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(key: String?, size: Int, orderBy: PostingOrder) = postingRepository.getNormalPostings(key, size, orderBy)
}