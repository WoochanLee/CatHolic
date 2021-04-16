package com.woody.cat.holic.presentation.main.user.profile.following

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.paging.FollowingListDataSource
import com.woody.cat.holic.usecase.user.GetUserProfile

class FollowingListViewModel(
    private val followingUserList: List<String>,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    fun getCommentFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            FollowingListDataSource(
                followingUserList,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }
}