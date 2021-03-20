package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository

class GetNextPostings(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(fromTheTop: Boolean, size: Int, orderBy: PostingOrder) = postingRepository.getNextPostings(fromTheTop, size, orderBy)
}