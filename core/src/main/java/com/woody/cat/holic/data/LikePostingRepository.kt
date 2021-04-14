package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface LikePostingRepository {
    suspend fun likePosting(userId: String, postingId: String): Resource<Unit>

    suspend fun unlikePosting(userId: String, postingId: String): Resource<Unit>
}