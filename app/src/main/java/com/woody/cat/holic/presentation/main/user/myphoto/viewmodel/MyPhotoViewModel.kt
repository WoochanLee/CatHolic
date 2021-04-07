package com.woody.cat.holic.presentation.main.user.myphoto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.base.printStackTraceIfDebug
import com.woody.cat.holic.framework.posting.UploadedPostingDataSource
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

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.postValue(Unit)
    }

    fun onClickDelete(postingId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = removeUserPosting(postingId)

                handleResourceResult(result, onSuccess = {
                    _eventRefreshData.postValue(Unit)
                }, onError = {
                    it.printStackTraceIfDebug()
                    //TODO : handle network error
                })
            }
        }
    }
}