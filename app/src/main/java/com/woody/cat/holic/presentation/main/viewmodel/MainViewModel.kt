package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.usecase.AddLikeInPosting
import com.woody.cat.holic.usecase.GetNextNormalPostings
import com.woody.cat.holic.usecase.RemoveLikeInPosting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class MainViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val getNextNormalPostings: GetNextNormalPostings,
    private val addLikeInPosting: AddLikeInPosting,
    private val removeLikeInPosting: RemoveLikeInPosting
) : BaseViewModel() {

    companion object {
        const val POSTING_PAGE_SIZE = 10
    }

    private val _eventStartUploadActivity = MutableLiveData<Unit>()
    val eventStartUploadActivity: LiveData<Unit> get() = _eventStartUploadActivity

    private val _eventMoveToSignInTabWithToast = MutableLiveData<Unit>()
    val eventMoveToSignInTabWithToast: LiveData<Unit> get() = _eventMoveToSignInTabWithToast

    private val _isVisibleUploadFab = MutableLiveData(true)
    val isVisibleUploadFab: LiveData<Boolean> get() = _isVisibleUploadFab

    private val _isVisibleOrderSwitch = MutableLiveData(true)
    val isVisibleOrderSwitch: LiveData<Boolean> get() = _isVisibleOrderSwitch

    private val _postingsLiveData = MutableLiveData<List<PostingItem>>()
    val postingsLiveData: LiveData<List<PostingItem>> get() = _postingsLiveData

    private val _currentPostingOrder = MutableLiveData(PostingOrder.LIKED)
    val currentPostingOrder: LiveData<PostingOrder> get() = _currentPostingOrder

    private var getNextPostingsJob: Job? = null

    fun initPostings() {
        getNextPostingsJob?.cancel(CancellationException())
        getNextPostingsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getNextNormalPostings(true, POSTING_PAGE_SIZE, currentPostingOrder.value ?: PostingOrder.LIKED)
                handleNetworkResult(result, onSuccess = { postingList ->
                    _postingsLiveData.postValue(postingList.map { it.mapToPostingItem(firebaseUserManager.getCurrentUser()?.userId) })
                }, onError = {
                    //TODO: handle network error
                })
            }
        }
    }

    fun changeToNextPostingOrder() {
        val nextOrder = when (currentPostingOrder.value) {
            PostingOrder.LIKED -> PostingOrder.CREATED
            PostingOrder.CREATED -> PostingOrder.RANDOM
            PostingOrder.RANDOM -> PostingOrder.LIKED
            null -> PostingOrder.LIKED
        }

        _currentPostingOrder.value = nextOrder
    }

    fun getResourceIdByPostingOrder(postingOrder: PostingOrder): Int {
        return when (postingOrder) {
            PostingOrder.LIKED -> R.drawable.ic_heart_fill
            PostingOrder.CREATED -> R.drawable.ic_clock_fill
            PostingOrder.RANDOM -> R.drawable.ic_shuffle
        }
    }

    fun onClickLike(postingItem: PostingItem) {
        val user = firebaseUserManager.getCurrentUser()
        if (user == null) {
            _eventMoveToSignInTabWithToast.postValue(Unit)
            return
        }

        val currentUserLiked = postingItem.currentUserLiked.value == true

        postingItem.currentUserLiked.postValue(!currentUserLiked)
        postingItem.liked.postValue((postingItem.liked.value ?: 0) + if (currentUserLiked) -1 else 1)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (currentUserLiked) {
                    removeLikeInPosting(user.userId, postingItem.postingId)
                } else {
                    addLikeInPosting(user.userId, postingItem.postingId)
                }
            }
        }
    }

    fun onClickUploadFab() {
        if (firebaseUserManager.isSignedIn()) {
            _eventStartUploadActivity.postValue(Unit)
        } else {
            _eventMoveToSignInTabWithToast.postValue(Unit)
        }
    }

    fun setVisibleUploadFab(isVisible: Boolean) {
        _isVisibleUploadFab.postValue(isVisible)
    }

    fun setVisibleOrderFab(isVisible: Boolean) {
        _isVisibleOrderSwitch.postValue(isVisible)
    }
}