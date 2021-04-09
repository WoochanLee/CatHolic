package com.woody.cat.holic.presentation.main.posting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.framework.base.BaseViewModel

class PostingDetailViewModel : BaseViewModel() {

    private val _isMenuVisible = MutableLiveData(true)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    fun onClickPostingDetailImage() {
        _isMenuVisible.postValue(isMenuVisible.value != true)
    }
}