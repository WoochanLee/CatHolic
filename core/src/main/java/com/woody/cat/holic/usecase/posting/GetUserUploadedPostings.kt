package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class GetUserUploadedPostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 20
    }

    suspend operator fun invoke(key: String?, userId: String) = postingRepository.getUserUploadedPostings(key, userId, PAGE_SIZE)
}