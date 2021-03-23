package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository

class GetLikePostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    fun getCurrentPostingOrder() = postingRepository.currentLikePostingOrder

    suspend fun getPostings(key: String?, isChangeToNextOrder: Boolean) = postingRepository.getLikePostings(key, PAGE_SIZE, isChangeToNextOrder)
}