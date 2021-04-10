package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Comment

interface CommentRepository {

    suspend fun addComment(comment: Comment): Resource<Unit>

    suspend fun getComments(key: String?, postingId: String): Resource<List<Comment>>
}