package com.woody.cat.holic.presentation.main.like.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.posting.LikePostingDataSource
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetUserLikePostings
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.flow.Flow

class LikeViewModel(
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserLikePostings: GetUserLikePostings,
    private val getUserProfile: GetUserProfile,
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

    private var isChangingToNextPostingOrder = false

    var dataSource: LikePostingDataSource? = null

    fun getLikedPostingFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            LikePostingDataSource(
                getCurrentUserId,
                getUserLikePostings,
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