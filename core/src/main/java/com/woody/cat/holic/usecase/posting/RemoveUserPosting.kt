package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class RemoveUserPosting(private val postingRepository: PostingRepository) {

    suspend operator fun invoke(userId: String, postingId: String) = postingRepository.removePosting(userId, postingId)
}