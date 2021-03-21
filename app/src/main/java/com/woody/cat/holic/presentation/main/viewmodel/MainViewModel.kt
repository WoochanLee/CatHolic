package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.presentation.main.MainTab
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.usecase.AddLikeInPosting
import com.woody.cat.holic.usecase.RemoveLikeInPosting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val addLikeInPosting: AddLikeInPosting,
    private val removeLikeInPosting: RemoveLikeInPosting
) : BaseViewModel() {

    var currentFragment = MainTab.TAB_GALLERY

    private val _eventStartUploadActivity = MutableLiveData<Unit>()
    val eventStartUploadActivity: LiveData<Unit> get() = _eventStartUploadActivity

    private val _eventMoveToSignInTabWithToast = MutableLiveData<Unit>()
    val eventMoveToSignInTabWithToast: LiveData<Unit> get() = _eventMoveToSignInTabWithToast

    private val _eventChangeGalleryPostingOrder = MutableLiveData<Unit>()
    val eventChangeGalleryPostingOrder: LiveData<Unit> get() = _eventChangeGalleryPostingOrder

    private val _eventChangeLikePostingOrder = MutableLiveData<Unit>()
    val eventChangeLikePostingOrder: LiveData<Unit> get() = _eventChangeLikePostingOrder

    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    private val _isVisibleUploadFab = MutableLiveData(true)
    val isVisibleUploadFab: LiveData<Boolean> get() = _isVisibleUploadFab

    private val _isVisibleOrderSwitch = MutableLiveData(true)
    val isVisibleOrderSwitch: LiveData<Boolean> get() = _isVisibleOrderSwitch

    private val _currentVisiblePostingOrder = MutableLiveData(PostingOrder.LIKED)
    val currentVisiblePostingOrder: LiveData<PostingOrder> get() = _currentVisiblePostingOrder

    fun setCurrentVisiblePostingOrder(postingOrder: PostingOrder) {
        _currentVisiblePostingOrder.postValue(postingOrder)
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.postValue(title)
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

    fun onClickChangePostingOrder() {
        if (currentFragment == MainTab.TAB_GALLERY) {
            _eventChangeGalleryPostingOrder.postValue(Unit)
        } else if (currentFragment == MainTab.TAB_LIKE) {
            _eventChangeLikePostingOrder.postValue(Unit)
        }
    }

    fun setVisibleUploadFab(isVisible: Boolean) {
        _isVisibleUploadFab.postValue(isVisible)
    }

    fun setVisibleOrderSwitch(isVisible: Boolean) {
        _isVisibleOrderSwitch.postValue(isVisible)
    }
}