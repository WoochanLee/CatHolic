package com.woody.cat.holic.presentation.main.posting.comment.viewmodel

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.posting.comment.AddComment
import com.woody.cat.holic.usecase.posting.comment.GetComments
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile

class CommentViewModelFactory(private val postingItem: PostingItem) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            return CommentViewModel(
                postingItem,
                GetCurrentUserId(userRepository),
                AddComment(commentRepository),
                GetComments(commentRepository),
                GetUserProfile(userRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}