package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.domain.Posting

class AddPosting(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(userId: String, postings: List<Posting>) = postingRepository.addPosting(userId, postings)
}