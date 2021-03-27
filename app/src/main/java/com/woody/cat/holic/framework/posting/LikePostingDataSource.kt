package com.woody.cat.holic.framework.posting

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.usecase.GetUserLikePostings

class LikePostingDataSource(
    private val currentUserId: String?,
    private val getUserPostings: GetUserLikePostings,
    private var isChangingToNextPostingOrder: Boolean
) : PagingSource<String, Posting>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Posting> {
        currentUserId?.let { userId ->
            getUserPostings.getPostings(params.key, userId, isChangingToNextPostingOrder).let { result ->
                isChangingToNextPostingOrder = false

                return if (result is Resource.Success) {
                    val postingList = result.data
                    LoadResult.Page(
                        data = postingList,
                        prevKey = null,
                        nextKey = postingList.lastOrNull()?.postingId
                    )
                } else {
                    LoadResult.Error((result as Resource.Error).exception)
                }
            }
        } ?: return LoadResult.Error(NotSignedInException())
    }

    override fun getRefreshKey(state: PagingState<String, Posting>): String? {
        return null
    }
}