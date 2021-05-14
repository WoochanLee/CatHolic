package com.woody.cat.holic.presentation.main.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.FollowingListDataSource
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FollowViewModel @Inject constructor(
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val getRefreshEventBus: RefreshEventBus
) : BaseViewModel() {

    private var followingUserList = emptyList<String>()

    companion object {
        const val PAGE_SIZE = 10
    }

    init {
        initEventBusSubscribe()
    }

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getFollowingListFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            FollowingListDataSource(
                followingUserList,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    private fun setIsListEmpty(isListEmpty: Boolean) {
        _isListEmpty.postValue(isListEmpty)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun initData() {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(userId), onSuccess = { user ->
                    followingUserList = user.followingUserIds
                    setIsListEmpty(user.followingCount == 0)
                    _eventRefreshData.emit()
                }, onError = {
                    //TODO: network error handle
                })
            }
        }
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            getRefreshEventBus.subscribeEvent(
                GlobalRefreshEvent.FollowUserEvent
            ) {
                initData()
            }
        }
    }
}