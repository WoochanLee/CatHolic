package com.woody.cat.holic.presentation.main.user.myphoto

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.R
import com.woody.cat.holic.data.common.PostingOrder
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.UploadedPostingDataSource
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.posting.GetPostingOrder
import com.woody.cat.holic.usecase.posting.GetUserUploadedPostings
import com.woody.cat.holic.usecase.posting.RemoveUserPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyPhotoViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserUploadedPostings: GetUserUploadedPostings,
    private val removeUserPosting: RemoveUserPosting,
    private val getUserProfile: GetUserProfile,
    private val getPostingOrder: GetPostingOrder,
    private val changeToNextPostingOrder: ChangeToNextPostingOrder
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _eventShowPostingDetail = MutableLiveData<Event<PostingItem>>()
    val eventShowPostingDetail: LiveData<Event<PostingItem>> get() = _eventShowPostingDetail

    private val _eventShowDeleteWarningDialog = MutableLiveData<Event<Pair<String, String>>>()
    val eventShowDeleteWarningDialog: LiveData<Event<Pair<String, String>>> get() = _eventShowDeleteWarningDialog

    private val _eventStartUploadActivity = MutableLiveData<Event<Unit>>()
    val eventStartUploadActivity: LiveData<Event<Unit>> get() = _eventStartUploadActivity

    private val _eventChangeUserPostingOrder = MutableLiveData<Event<Unit>>()
    val eventChangeUserPostingOrder: LiveData<Event<Unit>> get() = _eventChangeUserPostingOrder

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _currentVisiblePostingOrder = MutableLiveData(getPostingOrder.getMyPhotoPostingOrder())
    val currentVisiblePostingOrder: LiveData<PostingOrder> get() = _currentVisiblePostingOrder

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    val myPhotoItemMenuListener = object: MyPhotoItemMenuListener {
        override fun onClickDelete(postingId: String) {
            val userId = getCurrentUserId() ?: return

            _eventShowDeleteWarningDialog.emit(Pair(userId, postingId))
        }
    }

    init {
        initEventBusSubscribe()
    }

    fun getUploadedPostings() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            UploadedPostingDataSource(
                getCurrentUserId,
                getUserUploadedPostings,
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

    fun setIsListEmpty(isListEmpty: Boolean) {
        _isListEmpty.postValue(isListEmpty)
    }

    fun onClickPosting(postingItem: PostingItem) {
        _eventShowPostingDetail.emit(postingItem)
    }

    fun onClickUploadFab() {
        _eventStartUploadActivity.emit()
    }

    fun onClickChangePostingOrder() {
        _eventChangeUserPostingOrder.emit()
    }

    fun changeToNextPostingOrder() {
        changeToNextPostingOrder(PostingType.MY_PHOTO)
    }

    fun refreshVisiblePostingOrder() {
        _currentVisiblePostingOrder.postValue(getPostingOrder.getMyPhotoPostingOrder())
    }

    fun deletePosting(userId: String, postingId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(removeUserPosting(userId, postingId), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.DELETE_POSTING_EVENT)
                }, onError = {
                    it.printStackTraceIfDebug()
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.UPLOAD_POSTING_EVENT,
                GlobalRefreshEvent.DELETE_POSTING_EVENT
            ) {
                initData()
            }
        }
    }

    interface MyPhotoItemMenuListener {
        fun onClickDelete(postingId: String)
    }
}