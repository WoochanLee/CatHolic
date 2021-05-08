package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class GetSinglePosting(private val postingRepository: PostingRepository) {

    suspend operator fun invoke(postingId: String) = postingRepository.getSinglePosting(postingId)
}