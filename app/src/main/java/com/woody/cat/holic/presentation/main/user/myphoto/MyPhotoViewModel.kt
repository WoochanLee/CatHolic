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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

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

    fun onClickDelete(postingId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = removeUserPosting(postingId)

                handleResourceResult(result, onSuccess = {
                    _eventRefreshData.emit()
                }, onError = {
                    it.printStackTraceIfDebug()
                    //TODO : handle network error
                })
            }
        }
    }

    fun onClickComment(postingItem: PostingItem) {
        _eventShowCommentDialog.emit(postingItem)
    }

    fun onClickLikeList(postingItem: PostingItem) {
        _eventShowLikeListDialog.emit(postingItem)
    }
}