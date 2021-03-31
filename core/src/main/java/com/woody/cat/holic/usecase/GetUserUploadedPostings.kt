package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository

class GetUserUploadedPostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    suspend fun getPostings(key: String?, userId: String) = postingRepository.getUserUploadedPostings(key, userId, PAGE_SIZE)
}