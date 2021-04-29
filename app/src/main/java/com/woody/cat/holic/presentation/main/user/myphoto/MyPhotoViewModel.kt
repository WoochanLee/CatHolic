package com.woody.cat.holic.presentation.main.user.myphoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.UploadedPostingDataSource
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.posting.GetUserUploadedPostings
import com.woody.cat.holic.usecase.posting.RemoveUserPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPhotoViewModel(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserUploadedPostings: GetUserUploadedPostings,
    private val removeUserPosting: RemoveUserPosting,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _eventShowCommentDialog = MutableLiveData<Event<PostingItem>>()
    val eventShowCommentDialog: LiveData<Event<PostingItem>> get() = _eventShowCommentDialog

    private val _eventShowLikeListDialog = MutableLiveData<Event<PostingItem>>()
    val eventShowLikeListDialog: LiveData<Event<PostingItem>> get() = _eventShowLikeListDialog

    private val _eventStartUploadActivity = MutableLiveData<Event<Unit>>()
    val eventStartUploadActivity: LiveData<Event<Unit>> get() = _eventStartUploadActivity

    private val _eventStartPhotoDownload = MutableLiveData<Event<String>>()
    val eventStartPhotoDownload: LiveData<Event<String>> get() = _eventStartPhotoDownload

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    val myPhotoItemMenuListener = object: MyPhotoItemMenuListener {
        override fun onClickDelete(postingId: String) {
            val userId = getCurrentUserId() ?: return

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    handleResourceResult(removeUserPosting(userId, postingId), onSuccess = {
                        refreshEventBus.emitEvent(GlobalRefreshEvent.DeletePostingEvent)
                    }, onError = {
                        it.printStackTraceIfDebug()
                        //TODO : handle network error
                    })
                }
            }
        }

        override fun onClickDownload(imageUrl: String) {
            _eventStartPhotoDownload.emit(imageUrl)
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

    fun onClickComment(postingItem: PostingItem) {
        _eventShowCommentDialog.emit(postingItem)
    }

    fun onClickLikeList(postingItem: PostingItem) {
        _eventShowLikeListDialog.emit(postingItem)
    }

    fun onClickUploadFab() {
        _eventStartUploadActivity.emit()
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

    interface MyPhotoItemMenuListener {
        fun onClickDelete(postingId: String)
        fun onClickDownload(imageUrl: String)
    }
}