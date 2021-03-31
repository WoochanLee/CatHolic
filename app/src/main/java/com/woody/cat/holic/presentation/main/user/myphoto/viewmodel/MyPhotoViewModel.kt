package com.woody.cat.holic.presentation.main.user.myphoto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.framework.posting.UploadedPostingDataSource
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.usecase.GetUserUploadedPostings
import com.woody.cat.holic.usecase.RemoveUserPosting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPhotoViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val getUserUploadedPostings: GetUserUploadedPostings,
    private val removeUserPosting: RemoveUserPosting
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Unit>()
    val eventRefreshData: LiveData<Unit> get() = _eventRefreshData

    val flow = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        UploadedPostingDataSource(firebaseUserManager.getCurrentUser()?.userId, getUserUploadedPostings)
    }.flow.cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.postValue(Unit)
    }

    fun onClickMenu(postingId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = removeUserPosting(postingId)

                handleNetworkResult(result, onSuccess = {
                    _eventRefreshData.postValue(Unit)
                }, onError = {
                    it.printStackTrace()
                    //TODO : handle network error
                })
            }
        }
    }

    fun getCurrentUser() = firebaseUserManager.getCurrentUser()
}