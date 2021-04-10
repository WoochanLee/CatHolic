package com.woody.cat.holic.usecase.posting.comment

import com.woody.cat.holic.data.CommentRepository
import com.woody.cat.holic.domain.Comment

class AddComment(private val commentRepository: CommentRepository) {

    suspend operator fun invoke(comment: Comment) = commentRepository.addComment(comment)
}