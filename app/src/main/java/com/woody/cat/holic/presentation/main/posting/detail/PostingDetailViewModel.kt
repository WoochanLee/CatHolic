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
import com.woody.cat.holic.framework.paging.item.mapToPostingItem
import com.woody.cat.holic.framework.paging.item.updateUserItem
import com.woody.cat.holic.usecase.posting.GetSinglePosting
import com.woody.cat.holic.usecase.posting.ReportPosting
import com.woody.cat.holic.usecase.posting.UpdateLikedPosting
import com.woody.cat.holic.usecase.review.AddLikedCountForInAppReview
import com.woody.cat.holic.usecase.review.GetHaveToShowInAppReview
import com.woody.cat.holic.usecase.review.SetHaveToShowInAppReview
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
    private val getUserProfile: GetUserProfile,
    private val updateLikedPosting: UpdateLikedPosting,
    private val addLikedCountForInAppReview: AddLikedCountForInAppReview,
    private val getHaveToShowInAppReview: GetHaveToShowInAppReview,
    private val setHaveToShowInAppReview: SetHaveToShowInAppReview,
    private val getSinglePosting: GetSinglePosting,
    private val reportPosting: ReportPosting
) : BaseViewModel() {

    private val _postingItem = MutableLiveData<PostingItem>()
    val postingItem: LiveData<PostingItem> get() = _postingItem

    private val _eventShowCommentDialog = MutableLiveData<Event<Unit>>()
    val eventShowCommentDialog: LiveData<Event<Unit>> get() = _eventShowCommentDialog

    private val _eventSharePosting = MutableLiveData<Event<String>>()
    val eventSharePosting: LiveData<Event<String>> get() = _eventSharePosting

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _eventShowInAppReview = MutableLiveData<Event<Unit>>()
    val eventShowInAppReview: LiveData<Event<Unit>> get() = _eventShowInAppReview

    private val _eventShowLikeListDialog = MutableLiveData<Event<PostingItem>>()
    val eventShowLikeListDialog: LiveData<Event<PostingItem>> get() = _eventShowLikeListDialog

    private val _eventShowPostingDetailBottomSheet = MutableLiveData<Event<Unit>>()
    val eventShowPostingDetailBottomSheet: LiveData<Event<Unit>> get() = _eventShowPostingDetailBottomSheet

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    private val _eventStartDownloadPhoto = MutableLiveData<Event<String>>()
    val eventStartDownloadPhoto: LiveData<Event<String>> get() = _eventStartDownloadPhoto

    private val _eventDismissBottomSheet = MutableLiveData<Event<Unit>>()
    val eventDismissBottomSheet: LiveData<Event<Unit>> get() = _eventDismissBottomSheet

    private val _isMenuVisible = MutableLiveData(true)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    private val _isVisibleFollowButton = MutableLiveData(false)
    val isVisibleFollowButton: LiveData<Boolean> get() = _isVisibleFollowButton

    private val _isUserFollowed = MediatorLiveData<Boolean>()
    val isUserFollowed: LiveData<Boolean> get() = _isUserFollowed

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _currentSelectedImageUrl = MutableLiveData<String>()
    val currentSelectedImageUrl: LiveData<String> get() = _currentSelectedImageUrl

    init {
        initEventBusSubscribe()
    }

    fun addSourceToIsUserFollowed() {
        postingItem.value?.let { postingItem ->
            _isUserFollowed.addSource(postingItem.user.followerUserIds) {
                checkIsMyProfile(postingItem.user.userId)
                refreshIsUserFollowed(postingItem.user)
            }
        }
    }

    private fun refreshPostingItem() {
        postingItem.value?.postingId?.let { postingId ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    handleResourceResult(getSinglePosting(postingId), onSuccess = { posting ->
                        val postingItem = posting.mapToPostingItem(getCurrentUserId())
                        _postingItem.postValue(postingItem)
                        getProfile(postingItem.user.userId)
                    })
                }
            }
        }
    }

    private fun getProfile(targetUserId: String) {
        postingItem.value?.user?.let { userItem ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    handleResourceResult(getUserProfile(targetUserId), onSuccess = { targetUser ->
                        targetUser.updateUserItem(userItem)
                    })
                }
            }
        }
    }

    fun setPostingItem(postingItem: PostingItem) {
        _postingItem.value = postingItem
    }

    fun setCurrentSelectedImageUrl(imageUrl: String) {
        _currentSelectedImageUrl.postValue(imageUrl)
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
        _eventDismissBottomSheet.emit()
    }

    fun onClickDownload(imageUrl: String) {
        _eventStartDownloadPhoto.emit(imageUrl)
        _eventDismissBottomSheet.emit()
    }

    fun onClickReportPosting(postingId: String) {
        val currentUserId = getCurrentUserId()

        if(currentUserId == null) {
            _eventShowToast.emit(R.string.need_to_sign_in)
            return
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(reportPosting(currentUserId, postingId))
            }
        }
        _eventShowToast.emit(R.string.thank_you_for_reporting)
        _eventDismissBottomSheet.emit()
    }

    fun onClickHideBottomSheet() {
        _eventDismissBottomSheet.emit()
    }

    fun onClickFollowButton() {
        val myUserId = getCurrentUserId()
        val targetUserId = postingItem.value?.user?.userId
        if (myUserId == null || targetUserId == null) {
            _eventShowToast.emit(R.string.need_to_sign_in)
            return
        }

        followUser(myUserId, targetUserId)
    }

    fun onClickLike(postingItem: PostingItem) {
        val userId = getCurrentUserId()
        if (userId == null) {
            _eventShowToast.emit(R.string.need_to_sign_in)
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
                    addLikedCountForInAppReview()
                    checkInAppReviewStatus()
                }
                refreshEventBus.emitEvent(GlobalRefreshEvent.POSTING_LIKED_CHANGE_EVENT)
            }
        }
    }

    fun onClickLikeList(postingItem: PostingItem) {
        _eventShowLikeListDialog.emit(postingItem)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun onLongClickPostingImage() {
        _eventShowPostingDetailBottomSheet.emit()
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

    private fun checkInAppReviewStatus() {
        if (getHaveToShowInAppReview()) {
            setHaveToShowInAppReview(false)
            _eventShowInAppReview.emit()
        }
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.FOLLOW_USER_EVENT,
                GlobalRefreshEvent.POSTING_LIKED_CHANGE_EVENT
            ) {
                _isVisibleFollowButton.postValue(false)
                refreshPostingItem()
            }
        }
    }
}