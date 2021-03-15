package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.domain.Posting

class AddPostings(private val postingRepository: PostingRepository) {
    suspend operator fun invoke(postings: List<Posting>) = postingRepository.addPostings(postings)
}