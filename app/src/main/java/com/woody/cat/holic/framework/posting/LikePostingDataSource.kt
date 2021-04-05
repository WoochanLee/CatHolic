package com.woody.cat.holic.framework.posting

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.presentation.main.UserItem
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetUserLikePostings
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikePostingDataSource(
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserLikePostings: GetUserLikePostings,
    private val getUserProfile: GetUserProfile
) : PagingSource<String, PostingItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostingItem> {
        getCurrentUserId()?.let { userId ->
            getUserLikePostings(params.key, userId).let { result ->
                return if (result is Resource.Success) {
                    result.data
                        .map { mapToPostingItem(it, getCurrentUserId()) }
                        .let { postingList ->

                            postingList.forEach {
                                getPostingUserProfile(it.user)
                            }

                            LoadResult.Page(
                                data = postingList,
                                prevKey = null,
                                nextKey = postingList.lastOrNull()?.postingId
                            )
                        }
                } else {
                    LoadResult.Error((result as Resource.Error).exception)
                }
            }
        } ?: return LoadResult.Error(NotSignedInException())
    }

    private fun getPostingUserProfile(userItem: UserItem) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(userItem.userId), onSuccess = {
                    userItem.displayName.postValue(it.displayName)
                    userItem.userPhotoUrl.postValue(it.userPhotoUrl)
                })
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, PostingItem>): String? {
        return null
    }
}