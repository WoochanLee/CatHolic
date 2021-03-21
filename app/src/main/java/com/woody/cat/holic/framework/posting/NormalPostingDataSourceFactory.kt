package com.woody.cat.holic.framework.posting

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.usecase.GetNormalPostings
import kotlinx.coroutines.CoroutineScope

class NormalPostingDataSourceFactory(
    private val firebaseUserManager: FirebaseUserManager,
    private val postingRepository: PostingRepository,
    private var postingOrder: PostingOrder,
    private val scope: CoroutineScope
): DataSource.Factory<String, PostingItem>() {

    private val _sourceLiveData = MutableLiveData<NormalPostingDataSource>()

    override fun create(): DataSource<String, PostingItem> {
        val source = NormalPostingDataSource(firebaseUserManager, GetNormalPostings(postingRepository), scope, postingOrder)
        _sourceLiveData.postValue(source)
        return source
    }

    fun initData() {
        _sourceLiveData.value?.invalidate()
    }

    fun changePostingOrder(postingOrder: PostingOrder) {
        this.postingOrder = postingOrder
    }
}