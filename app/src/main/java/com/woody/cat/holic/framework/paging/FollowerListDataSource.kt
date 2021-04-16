package com.woody.cat.holic.framework.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.paging.item.UserItem
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowerListDataSource(
    private val followerUserList: List<String>,
    private val getUserProfile: GetUserProfile
) : PagingSource<Int, UserItem>() {

    companion object {
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserItem> {
        val key = params.key ?: 0
        val nextKey = if (key + PAGE_SIZE > followerUserList.size) followerUserList.size else key + PAGE_SIZE
        val list = followerUserList.subList(key, nextKey).map {
            UserItem(userId = it).apply { getPostingUserProfile(this) }
        }

        return LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = if (key == nextKey) null else nextKey
        )
    }

    private fun getPostingUserProfile(userItem: UserItem) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(userItem.userId), onSuccess = {
                    userItem.displayName.postValue(it.displayName)
                    userItem.userProfilePhotoUrl.postValue(it.userProfilePhotoUrl)
                })
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserItem>): Int {
        return 0
    }
}