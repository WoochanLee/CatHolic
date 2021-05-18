package com.woody.cat.holic.framework.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Notification
import com.woody.cat.holic.usecase.notification.GetNotifications

class NotificationDataSource(
    private val getNotifications: GetNotifications
) : PagingSource<Int, Notification>() {

    companion object {
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val key = params.key ?: 0
        getNotifications(PAGE_SIZE, key * PAGE_SIZE).let { result ->
            return if (result is Resource.Success) {
                LoadResult.Page(
                    data = result.data,
                    prevKey = null,
                    nextKey = if (result.data.isEmpty()) null else key + 1
                )
            } else {
                LoadResult.Error((result as Resource.Error).exception)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? = null
}