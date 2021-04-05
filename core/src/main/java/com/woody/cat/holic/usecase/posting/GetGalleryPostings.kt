package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class GetGalleryPostings(private val postingRepository: PostingRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    suspend operator fun invoke(key: String?) = postingRepository.getGalleryPostings(key, PAGE_SIZE)
}