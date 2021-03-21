package com.woody.cat.holic.framework.posting

import androidx.paging.ItemKeyedDataSource
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.usecase.GetLikePostings
import kotlinx.coroutines.*
import java.util.concurrent.CancellationException

class LikePostingDataSource(
    private val firebaseUserManager: FirebaseUserManager,
    private val getLikePostings: GetLikePostings,
    private val scope: CoroutineScope,
    private var postingOrder: PostingOrder
) : ItemKeyedDataSource<String, PostingItem>() {

    private var getPostingsJob: Job? = null

    override fun getKey(item: PostingItem): String {
        return item.postingId
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<PostingItem>) {
        val userId = firebaseUserManager.getCurrentUser()?.userId

        getPostingsJob?.cancel(CancellationException())
        getPostingsJob = scope.launch {
            withContext(Dispatchers.IO) {
                val result = getLikePostings(null, params.requestedLoadSize, postingOrder)
                handleNetworkResult(result, onSuccess = {
                    callback.onResult(it.map { posting -> posting.mapToPostingItem(userId) })
                }, {
                    scope.cancel("network error", it)
                })
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PostingItem>) {
        val userId = firebaseUserManager.getCurrentUser()?.userId

        scope.launch {
            withContext(Dispatchers.IO) {
                val result = getLikePostings(params.key, params.requestedLoadSize, postingOrder)
                handleNetworkResult(result, onSuccess = {
                    callback.onResult(it.map { posting -> posting.mapToPostingItem(userId) })
                }, {
                    scope.cancel("network error", it)
                })
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PostingItem>) = Unit
}