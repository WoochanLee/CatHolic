package com.woody.cat.holic.framework.posting

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.presentation.main.UserItem
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.posting.GetGalleryPostings
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryPostingDataSource(
    private val getCurrentUserId: GetCurrentUserId,
    private val getGalleryPostings: GetGalleryPostings,
    private val getUserProfile: GetUserProfile,
    private var isChangingToNextPostingOrder: Boolean
) : PagingSource<String, PostingItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostingItem> {
        getGalleryPostings(params.key, isChangingToNextPostingOrder).let { result ->
            isChangingToNextPostingOrder = false

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
    }

    private fun getPostingUserProfile(userItem: UserItem) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                handleNetworkResult(getUserProfile(userItem.userId), onSuccess = {
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