package com.woody.cat.holic.presentation.main.posting

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.manager.AndroidStringResourceManager
import com.woody.cat.holic.framework.manager.FirebaseDynamicLinkManager
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.framework.paging.item.UserItem
import com.woody.cat.holic.framework.paging.item.mapToPostingItem
import com.woody.cat.holic.framework.paging.item.updateUserItem
import com.woody.cat.holic.usecase.posting.GetSinglePosting
import com.woody.cat.holic.usecase.posting.UpdateLikedPosting
import com.woody.cat.holic.usecase.share.GetDynamicLink
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostingViewModel @Inject constructor(
    private val androidStringResourceManager: AndroidStringResourceManager,
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val updateLikedPosting: UpdateLikedPosting,
    private val getSinglePosting: GetSinglePosting,
    private val getUserProfile: GetUserProfile,
    private val getDynamicLink: GetDynamicLink
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

    private val _eventSharePosting = MutableLiveData<Event<String>>()
    val eventSharePosting: LiveData<Event<String>> get() = _eventSharePosting

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

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

    fun onClickShare(postingId: String, displayName: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deepLink = "${FirebaseDynamicLinkManager.SHARE_PHOTO_PREFIX}?postingId=$postingId"

                handleResourceResult(
                    getDynamicLink(
                        deepLink = deepLink,
                        description = androidStringResourceManager.getString(R.string.s_cat_photo, displayName),
                        imageUrl = imageUrl
                    ),
                    onSuccess = { dynamicLink ->
                        _eventSharePosting.emit(dynamicLink)
                    })
            }
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
        postingItem.likeCount.postValue((postingItem.likeCount.value ?: 0) + if (currentUserLiked) -1 else 1)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (currentUserLiked) {
                    updateLikedPosting.unlikePosting(userId, postingItem.postingId)
                } else {
                    updateLikedPosting.likePosting(userId, postingItem.postingId)
                }
                refreshEventBus.emitEvent(GlobalRefreshEvent.POSTING_LIKED_CHANGE_EVENT)
            }
        }
    }

    fun handleDeepLinkToPostingDetail(postingId: String, showComment: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getSinglePosting(postingId), onSuccess = { posting ->
                    val postingItem = posting.mapToPostingItem(getCurrentUserId())
                    getPostingUserProfile(postingItem.user)
                    _eventShowPostingDetail.emit(postingItem)

                    if (showComment) {
                        _eventShowCommentDialog.emit(postingItem)
                    }
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    private fun getPostingUserProfile(userItem: UserItem) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(userItem.userId), onSuccess = { user ->
                    user.updateUserItem(userItem)
                })
            }
        }
    }
}