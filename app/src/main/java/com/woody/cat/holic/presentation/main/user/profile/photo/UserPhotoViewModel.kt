package com.woody.cat.holic.presentation.main.user.profile.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.paging.UserPostingDataSource
import com.woody.cat.holic.usecase.posting.GetUserPostings
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPhotoViewModel @Inject constructor(
    private val getUserPostings: GetUserPostings,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    lateinit var userId: String

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    private val _userDisplayName = MutableLiveData("")
    val userDisplayName: LiveData<String> get() = _userDisplayName

    fun getProfile() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(userId), onSuccess = { user ->
                    _userDisplayName.postValue(user.displayName ?: "")
                })
            }
        }
    }

    fun getUserPostings() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            UserPostingDataSource(
                userId,
                getUserPostings,
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

}