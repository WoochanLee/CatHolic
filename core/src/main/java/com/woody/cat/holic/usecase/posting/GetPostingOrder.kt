package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository

class GetPostingOrder(private val postingRepository: PostingRepository) {

    fun getGalleryPostingOrder() = postingRepository.currentGalleryPostingOrder

    fun getLikePostingOrder() = postingRepository.currentLikePostingOrder

    fun getUserPostingOrder() = postingRepository.currentUserPostingOrder

    fun getMyPhotoPostingOrder() = postingRepository.currentMyPhotoPostingOrder
}