package com.woody.cat.holic.presentation.main.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.LikePostingDataSource
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.posting.GetUserLikePostings
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.launch
import javax.inject.Inject

class LikeViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val changeToNextPostingOrder: ChangeToNextPostingOrder,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserLikePostings: GetUserLikePostings,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    init {
        initEventBusSubscribe()
    }

    fun getLikedPostingFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            LikePostingDataSource(
                getCurrentUserId,
                getUserLikePostings,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun initData() {
        _eventRefreshData.emit()
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun setIsListEmpty(isListEmpty: Boolean) {
        _isListEmpty.postValue(isListEmpty)
    }

    fun changeToNextPostingOrder() {
        changeToNextPostingOrder(PostingType.LIKED)
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.PostingLikedChangeEvent,
                GlobalRefreshEvent.DeletePostingEvent
            ) {
                initData()
            }
        }
    }
}