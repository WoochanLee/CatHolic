package com.woody.cat.holic.presentation.main.like.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.posting.LikePostingDataSource
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.usecase.GetUserLikePostings

class LikeViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val getUserPostings: GetUserLikePostings
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

    private var isChangingToNextPostingOrder = false
    val flow = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        LikePostingDataSource(
            firebaseUserManager.getCurrentUser()?.userId,
            getUserPostings,
            isChangingToNextPostingOrder
        ).apply { isChangingToNextPostingOrder = false }
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
        isChangingToNextPostingOrder = true
    }

    fun getCurrentPostingOrder() = getUserPostings.getCurrentPostingOrder()
}