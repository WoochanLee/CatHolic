package com.woody.cat.holic.presentation.main.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.GalleryPostingDataSource
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.posting.GetGalleryPostings
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.launch
import javax.inject.Inject

class GalleryViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val changeToNextPostingOrder: ChangeToNextPostingOrder,
    private val getCurrentUserId: GetCurrentUserId,
    private val getGalleryPostings: GetGalleryPostings,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    init {
        initEventBusSubscribe()
    }

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getGalleryPostingFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            GalleryPostingDataSource(
                getCurrentUserId,
                getGalleryPostings,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.emit()
    }

    fun changeToNextPostingOrder() {
        changeToNextPostingOrder(PostingType.GALLERY)
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.UploadPostingEvent,
                GlobalRefreshEvent.DeletePostingEvent
            ) {
                initData()
            }
        }
    }
}