package com.woody.cat.holic.presentation.main.posting.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.manager.AndroidStringResourceManager
import com.woody.cat.holic.framework.manager.FirebaseDynamicLinkManager
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.framework.paging.item.UserItem
import com.woody.cat.holic.framework.paging.item.updateUserItem
import com.woody.cat.holic.usecase.share.GetDynamicLink
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.user.UpdateFollowUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostingDetailViewModel @Inject constructor(
    private val androidStringResourceManager: AndroidStringResourceManager,
    private val getDynamicLink: GetDynamicLink,
    private val getCurrentUserId: GetCurrentUserId,
    private val updateFollowUser: UpdateFollowUser,
    private val refreshEventBus: RefreshEventBus,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    lateinit var postingItem: PostingItem

    private val _eventShowCommentDialog = MutableLiveData<Event<Unit>>()
    val eventShowCommentDialog: LiveData<Event<Unit>> get() = _eventShowCommentDialog

    private val _eventSharePosting = MutableLiveData<Event<String>>()
    val eventSharePosting: LiveData<Event<String>> get() = _eventSharePosting

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _isMenuVisible = MutableLiveData(true)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    private val _isVisibleFollowButton = MutableLiveData(false)
    val isVisibleFollowButton: LiveData<Boolean> get() = _isVisibleFollowButton

    private val _isUserFollowed = MediatorLiveData<Boolean>()
    val isUserFollowed: LiveData<Boolean> get() = _isUserFollowed

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        initEventBusSubscribe()
    }

    fun addSourceToIsUserFollowed() {
        _isUserFollowed.addSource(postingItem.user.followerUserIds) {
            checkIsMyProfile(postingItem.user.userId)
            refreshIsUserFollowed(postingItem.user)
        }
    }

    fun onClickPostingDetailImage() {
        _isMenuVisible.postValue(isMenuVisible.value != true)
    }

    fun onClickComment() {
        _eventShowCommentDialog.emit()
    }

    fun onClickShare(postingId: String, title: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deepLink = "${FirebaseDynamicLinkManager.SHARE_PHOTO_PREFIX}?postingId=$postingId"

                handleResourceResult(
                    getDynamicLink(
                        deepLink = deepLink,
                        description = androidStringResourceManager.getString(R.string.s_cat_photo, title),
                        imageUrl = imageUrl
                    ),
                    onSuccess = { dynamicLink ->
                        _eventSharePosting.emit(dynamicLink)
                    })
            }
        }
    }

    fun onClickFollowButton() {
        val myUserId = getCurrentUserId()
        val targetUserId = postingItem.user.userId
        if (myUserId == null) {
            _eventShowToast.emit(R.string.need_to_sign_in)
            return
        }

        followUser(myUserId, targetUserId)
    }

    fun getProfile(targetUserId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(targetUserId), onSuccess = { targetUser ->
                    targetUser.updateUserItem(postingItem.user)
                    //refreshIsUserFollowed(postingItem.user)
                })
            }
        }
    }

    private fun refreshIsUserFollowed(targetUser: UserItem) {
        val myUserId = getCurrentUserId()

        _isUserFollowed.postValue(targetUser.followerUserIds.value?.contains(myUserId) ?: false)
    }

    private fun followUser(myUserId: String, targetUserId: String) {
        _isUserFollowed.postValue(isUserFollowed.value != true)

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateFollowUser.followUser(myUserId, targetUserId), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.FOLLOW_USER_EVENT)
                }, onError = {
                    _eventShowToast.emit(R.string.something_went_wrong)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    private fun checkIsMyProfile(userId: String) {
        val currentUserId = getCurrentUserId()
        _isVisibleFollowButton.postValue(currentUserId != userId)
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(GlobalRefreshEvent.FOLLOW_USER_EVENT) {
                _isVisibleFollowButton.postValue(false)
                getProfile(postingItem.user.userId)
            }
        }
    }
}