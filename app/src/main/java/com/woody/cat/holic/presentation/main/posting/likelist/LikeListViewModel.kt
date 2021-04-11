package com.woody.cat.holic.presentation.main.posting.likelist

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.paging.LikeListDataSource
import com.woody.cat.holic.usecase.user.GetUserProfile

class LikeListViewModel(
    private val likeUserList: List<String>,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    fun getCommentFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            LikeListDataSource(
                likeUserList,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)
}