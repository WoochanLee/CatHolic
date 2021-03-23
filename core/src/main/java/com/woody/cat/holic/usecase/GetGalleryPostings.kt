package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PostingRepository

class GetGalleryPostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    fun getCurrentPostingOrder() = postingRepository.currentGalleryPostingOrder

    suspend fun getPostings(key: String?, isChangeToNextOrder: Boolean) = postingRepository.getGalleryPostings(key, PAGE_SIZE, isChangeToNextOrder)
}