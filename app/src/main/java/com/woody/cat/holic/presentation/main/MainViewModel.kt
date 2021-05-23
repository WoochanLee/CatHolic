package com.woody.cat.holic.presentation.main

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.R
import com.woody.cat.holic.data.common.PostingOrder
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.usecase.posting.GetPostingOrder
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.setting.UpdateAppSetting
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getPostingOrder: GetPostingOrder,
    private val getIsSignedIn: GetIsSignedIn,
    private val getAppSetting: GetAppSetting,
    private val updateAppSetting: UpdateAppSetting,
) : BaseViewModel() {

    companion object {
        const val BACK_KEY_APP_CLOSE_DELAY = 3000
    }

    var currentTab = MainTab.TAB_GALLERY

    private val _eventStartUploadActivity = MutableLiveData<Event<Unit>>()
    val eventStartUploadActivity: LiveData<Event<Unit>> get() = _eventStartUploadActivity

    private val _eventMoveToSignInTabWithToast = MutableLiveData<Event<Unit>>()
    val eventMoveToSignInTabWithToast: LiveData<Event<Unit>> get() = _eventMoveToSignInTabWithToast

    private val _eventChangeGalleryPostingOrder = MutableLiveData<Event<Unit>>()
    val eventChangeGalleryPostingOrder: LiveData<Event<Unit>> get() = _eventChangeGalleryPostingOrder

    private val _eventChangeLikePostingOrder = MutableLiveData<Event<Unit>>()
    val eventChangeLikePostingOrder: LiveData<Event<Unit>> get() = _eventChangeLikePostingOrder

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    private val _eventShowNotificationListDialog = MutableLiveData<Event<Unit>>()
    val eventShowNotificationDialog: LiveData<Event<Unit>> get() = _eventShowNotificationListDialog

    private val _eventMoveToFollowTab = MutableLiveData<Event<Unit>>()
    val eventMoveToFollowTab: LiveData<Event<Unit>> get() = _eventMoveToFollowTab

    private val _eventMoveToLikeTab = MutableLiveData<Event<Unit>>()
    val eventMoveToLikeTab: LiveData<Event<Unit>> get() = _eventMoveToLikeTab

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _eventFinishApp = MutableLiveData<Event<Unit>>()
    val eventFinishApp: LiveData<Event<Unit>> get() = _eventFinishApp

    private val _isVisibleGuide = MutableLiveData(getAppSetting.getMainGuideStatus())
    val isVisibleGuide: LiveData<Boolean> get() = _isVisibleGuide

    private val _toolbarTitle = MutableLiveData<String>()
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    private val _isVisibleUploadFab = MutableLiveData(true)
    val isVisibleUploadFab: LiveData<Boolean> get() = _isVisibleUploadFab

    private val _isVisibleOrderSwitch = MutableLiveData(true)
    val isVisibleOrderSwitch: LiveData<Boolean> get() = _isVisibleOrderSwitch

    private val _isVisibleNotification = MutableLiveData(true)
    val isVisibleNotification: LiveData<Boolean> get() = _isVisibleNotification

    private val _isVisibleEditProfile = MutableLiveData(false)
    val isVisibleEditProfile: LiveData<Boolean> get() = _isVisibleEditProfile

    private val _currentVisiblePostingOrder = MutableLiveData(getPostingOrder.getGalleryPostingOrder())
    val currentVisiblePostingOrder: LiveData<PostingOrder> get() = _currentVisiblePostingOrder

    private var lastBackKeyPressedTimeMills = 0L

    fun refreshVisiblePostingOrder(mainTab: MainTab) {
        when (mainTab) {
            MainTab.TAB_GALLERY -> _currentVisiblePostingOrder.postValue(getPostingOrder.getGalleryPostingOrder())
            MainTab.TAB_LIKE -> _currentVisiblePostingOrder.postValue(getPostingOrder.getLikePostingOrder())
            else -> Unit
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

    fun onClickUploadFab() {
        if (getIsSignedIn()) {
            _eventStartUploadActivity.emit()
        } else {
            _eventMoveToSignInTabWithToast.emit()
        }
    }

    fun onClickFollowTab() {
        if (getIsSignedIn()) {
            _eventMoveToFollowTab.emit()
        } else {
            _eventMoveToSignInTabWithToast.emit()
        }
    }

    fun onClickLikeTab() {
        if (getIsSignedIn()) {
            _eventMoveToLikeTab.emit()
        } else {
            _eventMoveToSignInTabWithToast.emit()
        }
    }

    fun onClickChangePostingOrder() {
        if (currentTab == MainTab.TAB_GALLERY) {
            _eventChangeGalleryPostingOrder.emit()
        } else if (currentTab == MainTab.TAB_LIKE) {
            _eventChangeLikePostingOrder.emit()
        }
    }

    fun onClickNotificationList() {
        _eventShowNotificationListDialog.emit()
    }

    fun onClickProfileEdit(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun onClickCloseGuide() {
        updateAppSetting.setMainGuideStatus(false)
        _isVisibleGuide.postValue(false)
    }

    fun handleBackKeyFinish() {
        val currentTimeMills = Date().time
        if (lastBackKeyPressedTimeMills + BACK_KEY_APP_CLOSE_DELAY > currentTimeMills) {
            _eventFinishApp.emit()
        } else {
            lastBackKeyPressedTimeMills = currentTimeMills
            _eventShowToast.emit(R.string.press_back_again_to_exit)
        }
    }

    fun setVisibleUploadFab(isVisible: Boolean) {
        _isVisibleUploadFab.postValue(isVisible)
    }

    fun setVisibleOrderSwitch(isVisible: Boolean) {
        _isVisibleOrderSwitch.postValue(isVisible)
    }

    fun setVisibleNotification(isVisible: Boolean) {
        _isVisibleNotification.postValue(isVisible)
    }

    fun setVisibleEditProfile(isVisible: Boolean) {
        _isVisibleEditProfile.postValue(isVisible)
    }

    fun setMainGuideStatus(isVisible: Boolean) {
        updateAppSetting.setMainGuideStatus(isVisible)
    }

    fun setVisibleGuide(isVisible: Boolean) {
        _isVisibleGuide.postValue(isVisible)
    }

    fun callEventMoveToSignInTabWithToast() {
        _eventMoveToSignInTabWithToast.emit()
    }
}