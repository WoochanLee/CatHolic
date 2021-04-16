package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class GetUserPostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    suspend operator fun invoke(key: String?, userId: String) = postingRepository.getUserPostings(key, userId, PAGE_SIZE)
}