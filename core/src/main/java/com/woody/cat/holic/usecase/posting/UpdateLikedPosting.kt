package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.LikePostingRepository

class UpdateLikedPosting(private val likePostingRepository: LikePostingRepository) {
    suspend fun likePosting(userId: String, postingId: String) = likePostingRepository.likePosting(userId, postingId)

    suspend fun unlikePosting(userId: String, postingId: String) = likePostingRepository.unlikePosting(userId, postingId)
}