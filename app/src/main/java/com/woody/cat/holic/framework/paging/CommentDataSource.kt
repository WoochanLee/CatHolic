package com.woody.cat.holic.framework.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.paging.item.CommentItem
import com.woody.cat.holic.framework.paging.item.UserItem
import com.woody.cat.holic.framework.paging.item.mapToCommentItem
import com.woody.cat.holic.usecase.posting.comment.GetComments
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentDataSource(
    private val postingId: String,
    private val getComments: GetComments,
    private val getUserProfile: GetUserProfile
) : PagingSource<String, CommentItem>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommentItem> {
        getComments(params.key, postingId).let { result ->
            return if (result is Resource.Success) {
                result.data
                    .map { it.mapToCommentItem() }
                    .let { commentList ->

                        commentList.forEach {
                            getPostingUserProfile(it.user)
                        }

                        LoadResult.Page(
                            data = commentList,
                            prevKey = null,
                            nextKey = commentList.lastOrNull()?.commentId
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
                handleResourceResult(getUserProfile(userItem.userId), onSuccess = {
                    userItem.displayName.postValue(it.displayName)
                    userItem.userPhotoUrl.postValue(it.userPhotoUrl)
                })
            }
        }
    }

    override fun getRefreshKey(state: PagingState<String, CommentItem>): String? {
        return null
    }
}