package com.woody.cat.holic.usecase.posting.comment

import com.woody.cat.holic.data.CommentRepository

class GetComments(private val commentRepository: CommentRepository) {
    companion object {
        const val PAGE_SIZE = 10
    }

    suspend operator fun invoke(key: String?, postingId: String) = commentRepository.getComments(key, postingId, PAGE_SIZE)
}