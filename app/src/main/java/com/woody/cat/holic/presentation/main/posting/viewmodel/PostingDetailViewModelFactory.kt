package com.woody.cat.holic.presentation.main.posting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostingDetailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostingDetailViewModel::class.java)) {
            return PostingDetailViewModel() as T
        } else {
            throw IllegalStateException()
        }
    }
}