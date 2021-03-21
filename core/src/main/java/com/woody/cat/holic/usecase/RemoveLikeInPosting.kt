package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository

class RemoveLikeInPosting(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(userId: String, postingId: String) = postingRepository.removeLikedInPosting(userId, postingId)
}