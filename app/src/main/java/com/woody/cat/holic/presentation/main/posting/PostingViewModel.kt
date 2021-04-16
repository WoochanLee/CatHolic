package com.woody.cat.holic.presentation.main.posting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.posting.UpdateLikedPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostingViewModel(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val updateLikedPosting: UpdateLikedPosting
) : BaseViewModel() {

    private val _eventMoveToSignInTabWithToast = MutableLiveData<Event<Unit>>()
    val eventMoveToSignInTabWithToast: LiveData<Event<Unit>> get() = _eventMoveToSignInTabWithToast

    private val _eventShowPostingDetail = MutableLiveData<Event<PostingItem>>()
    val eventShowPostingDetail: LiveData<Event<PostingItem>> get() = _eventShowPostingDetail

    private val _eventShowCommentDialog = MutableLiveData<Event<PostingItem>>()
    val eventShowCommentDialog: LiveData<Event<PostingItem>> get() = _eventShowCommentDialog

    private val _eventShowLikeListDialog = MutableLiveData<Event<PostingItem>>()
    val eventShowLikeListDialog: LiveData<Event<PostingItem>> get() = _eventShowLikeListDialog

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    fun onClickPostingImage(postingItem: PostingItem) {
        _eventShowPostingDetail.emit(postingItem)
    }

    fun onClickComment(postingItem: PostingItem) {
        _eventShowCommentDialog.emit(postingItem)
    }

    fun onClickLikeList(postingItem: PostingItem) {
        _eventShowLikeListDialog.emit(postingItem)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun onClickLike(postingItem: PostingItem) {
        val userId = getCurrentUserId()
        if (userId == null) {
            _eventMoveToSignInTabWithToast.emit()
            return
        }

        val currentUserLiked = postingItem.currentUserLiked.value == true

        postingItem.currentUserLiked.postValue(!currentUserLiked)
        postingItem.likeCount.postValue((postingItem.likeCount.value ?: 0) + if (currentUserLiked) -1 else 1)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (currentUserLiked) {
                    updateLikedPosting.unlikePosting(userId, postingItem.postingId)
                } else {
                    updateLikedPosting.likePosting(userId, postingItem.postingId)
                }
                refreshEventBus.emitEvent(GlobalRefreshEvent.PostingLikedChangeEvent)
            }
        }
    }
}