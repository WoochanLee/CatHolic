package com.woody.cat.holic.presentation.main.posting.detail

import androidx.lifecycle.ViewModel
import com.woody.cat.holic.framework.base.BaseViewModelFactory
import com.woody.cat.holic.framework.paging.item.PostingItem

class PostingDetailViewModelFactory(private val postingItem: PostingItem) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostingDetailViewModel::class.java)) {
            return PostingDetailViewModel(postingItem) as T
        } else {
            throw IllegalStateException()
        }
    }
}