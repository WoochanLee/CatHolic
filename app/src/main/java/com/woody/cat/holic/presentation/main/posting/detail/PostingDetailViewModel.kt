package com.woody.cat.holic.presentation.main.posting.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.paging.item.PostingItem
import javax.inject.Inject

class PostingDetailViewModel @Inject constructor() : BaseViewModel() {

    lateinit var postingItem: PostingItem

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