package com.woody.cat.holic.presentation.main.gallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.posting.GalleryPostingDataSource
import com.woody.cat.holic.framework.posting.LikePostingDataSource
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetGalleryPostings
import com.woody.cat.holic.usecase.user.GetUserProfile

class GalleryViewModel(
    private val getCurrentUserId: GetCurrentUserId,
    private val getGalleryPostings: GetGalleryPostings,
    private val getUserProfile: GetUserProfile,
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

    private var isChangingToNextPostingOrder = false

    var dataSource: GalleryPostingDataSource? = null

    fun getGalleryPostingFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            GalleryPostingDataSource(
                getCurrentUserId,
                getGalleryPostings,
                getUserProfile,
                isChangingToNextPostingOrder
            ).apply {
                dataSource = this
                isChangingToNextPostingOrder = false
            }
        }
    ).flow.cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.postValue(Unit)
    }

    fun changeToNextPostingOrder() {
        isChangingToNextPostingOrder = true
    }
}