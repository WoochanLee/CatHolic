package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository

class RemoveUserPosting(private val postingRepository: PostingRepository) {

    suspend operator fun invoke(postingId: String) = postingRepository.removePosting(postingId)
}