package com.woody.cat.holic.presentation.main.posting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.item.PostingItem

class PostingDetailViewModel(val postingItem: PostingItem) : BaseViewModel() {

    private val _eventShowCommentDialog = MutableLiveData<Event<Unit>>()
    val eventShowCommentDialog: LiveData<Event<Unit>> get() = _eventShowCommentDialog

    private val _isMenuVisible = MutableLiveData(true)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    fun onClickPostingDetailImage() {
        _isMenuVisible.postValue(isMenuVisible.value != true)
    }

    fun onClickComment() {
        _eventShowCommentDialog.emit()
    }
}