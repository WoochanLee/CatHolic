package com.woody.cat.holic.presentation.main.gallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.posting.NormalPostingDataSourceFactory
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.presentation.main.PostingItem

class GalleryViewModel(firebaseUserManager: FirebaseUserManager, postingRepository: PostingRepository) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    var currentPostingOrder = PostingOrder.LIKED
        private set

    private val postingDataSourceFactory = NormalPostingDataSourceFactory(firebaseUserManager, postingRepository, currentPostingOrder, viewModelScope)

    private val _postingList: LiveData<PagedList<PostingItem>> = postingDataSourceFactory.toLiveData(
        Config(pageSize = PAGE_SIZE, initialLoadSizeHint = PAGE_SIZE)
    )
    val postingList: LiveData<PagedList<PostingItem>> get() = _postingList

    fun changeToNextPostingOrder() {
        currentPostingOrder = when (currentPostingOrder) {
            PostingOrder.LIKED -> PostingOrder.CREATED
            PostingOrder.CREATED -> PostingOrder.RANDOM
            PostingOrder.RANDOM -> PostingOrder.LIKED
        }

        postingDataSourceFactory.changePostingOrder(currentPostingOrder)
        initData()
    }

    fun initData() {
        postingDataSourceFactory.initData()
    }
}