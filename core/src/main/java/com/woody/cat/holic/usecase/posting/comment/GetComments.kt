package com.woody.cat.holic.usecase.posting.comment

import com.woody.cat.holic.data.CommentRepository

class GetComments(private val commentRepository: CommentRepository) {
    suspend operator fun invoke(key: String?, postingId: String) = commentRepository.getComments(key, postingId)
}