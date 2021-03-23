package com.woody.cat.holic.framework.posting

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.usecase.GetGalleryPostings

class GalleryPostingDataSource(private val getPostings: GetGalleryPostings, private var isNeedToChangeToNextPostingOrder: Boolean) : PagingSource<String, Posting>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Posting> {
        getPostings.getPostings(params.key, isNeedToChangeToNextPostingOrder).let { result ->
            isNeedToChangeToNextPostingOrder = false

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
    }

    override fun getRefreshKey(state: PagingState<String, Posting>): String? {
        return null
    }
}