package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.presentation.main.MainTab
import com.woody.cat.holic.presentation.main.PostingItem
import com.woody.cat.holic.usecase.posting.AddLikeInPosting
import com.woody.cat.holic.usecase.posting.GetPostingOrder
import com.woody.cat.holic.usecase.posting.RemoveLikeInPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val getPostingOrder: GetPostingOrder,
    private val getIsSignedIn: GetIsSignedIn,
    private val getCurrentUserId: GetCurrentUserId,
    private val addLikeInPosting: AddLikeInPosting,
    private val removeLikeInPosting: RemoveLikeInPosting
) : BaseViewModel() {

    var currentFragment = MainTab.TAB_GALLERY

    private val _eventStartUploadActivity = MutableLiveData<Event<Unit>>()
    val eventStartUploadActivity: LiveData<Event<Unit>> get() = _eventStartUploadActivity

    private val _eventMoveToSignInTabWithToast = MutableLiveData<Event<Unit>>()
    val eventMoveToSignInTabWithToast: LiveData<Event<Unit>> get() = _eventMoveToSignInTabWithToast

    private val _eventChangeGalleryPostingOrder = MutableLiveData<Event<Unit>>()
    val eventChangeGalleryPostingOrder: LiveData<Event<Unit>> get() = _eventChangeGalleryPostingOrder

    private val _eventChangeLikePostingOrder = MutableLiveData<Event<Unit>>()
    val eventChangeLikePostingOrder: LiveData<Event<Unit>> get() = _eventChangeLikePostingOrder

    private val _eventShowPostingDetail = MutableLiveData<Event<PostingItem>>()
    val eventShowPostingDetail: LiveData<Event<PostingItem>> get() = _eventShowPostingDetail

    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    private val _isVisibleUploadFab = MutableLiveData(true)
    val isVisibleUploadFab: LiveData<Boolean> get() = _isVisibleUploadFab

    private val _isVisibleOrderSwitch = MutableLiveData(true)
    val isVisibleOrderSwitch: LiveData<Boolean> get() = _isVisibleOrderSwitch

    private val _currentVisiblePostingOrder = MutableLiveData(PostingOrder.LIKED)
    val currentVisiblePostingOrder: LiveData<PostingOrder> get() = _currentVisiblePostingOrder

    fun refreshVisiblePostingOrder(mainTab: MainTab) {
        when (mainTab) {
            MainTab.TAB_GALLERY -> _currentVisiblePostingOrder.postValue(getPostingOrder.getGalleryPostingOrder())
            MainTab.TAB_LIKE -> _currentVisiblePostingOrder.postValue(getPostingOrder.getLikePostingOrder())
            MainTab.TAB_USER -> Unit
        }
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
        val userId = getCurrentUserId()
        if (userId == null) {
            _eventMoveToSignInTabWithToast.emit()
            return
        }

        val currentUserLiked = postingItem.currentUserLiked.value == true

        postingItem.currentUserLiked.postValue(!currentUserLiked)
        postingItem.liked.postValue((postingItem.liked.value ?: 0) + if (currentUserLiked) -1 else 1)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (currentUserLiked) {
                    removeLikeInPosting(userId, postingItem.postingId)
                } else {
                    addLikeInPosting(userId, postingItem.postingId)
                }
            }
        }
    }

    fun onClickUploadFab() {
        if (getIsSignedIn()) {
            _eventStartUploadActivity.emit()
        } else {
            _eventMoveToSignInTabWithToast.emit()
        }
    }

    fun onClickChangePostingOrder() {
        if (currentFragment == MainTab.TAB_GALLERY) {
            _eventChangeGalleryPostingOrder.emit()
        } else if (currentFragment == MainTab.TAB_LIKE) {
            _eventChangeLikePostingOrder.emit()
        }
    }

    fun onClickPostingImage(postingItem: PostingItem) {
        _eventShowPostingDetail.emit(postingItem)
    }

    fun setVisibleUploadFab(isVisible: Boolean) {
        _isVisibleUploadFab.postValue(isVisible)
    }

    fun setVisibleOrderSwitch(isVisible: Boolean) {
        _isVisibleOrderSwitch.postValue(isVisible)
    }
}