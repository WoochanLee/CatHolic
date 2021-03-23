package com.woody.cat.holic.presentation.main.gallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.posting.GalleryPostingDataSource
import com.woody.cat.holic.usecase.GetGalleryPostings

class GalleryViewModel(private val getPostings: GetGalleryPostings) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

    private var isNeedToChangeToNextPostingOrder = false

    val flow = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        GalleryPostingDataSource(getPostings, isNeedToChangeToNextPostingOrder).apply { isNeedToChangeToNextPostingOrder = false }
    }.flow.cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.postValue(Unit)
    }

    fun changeToNextPostingOrder() {
        isNeedToChangeToNextPostingOrder = true
    }

    fun getCurrentPostingOrder() = getPostings.getCurrentPostingOrder()
}