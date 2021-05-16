package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.BuildConfig
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.manager.AndroidStringResourceManager
import com.woody.cat.holic.framework.manager.FirebaseDynamicLinkManager
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.setting.UpdateAppSetting
import com.woody.cat.holic.usecase.share.GetDynamicLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private val androidStringResourceManager: AndroidStringResourceManager,
    private val getDynamicLink: GetDynamicLink,
    private val getAppSetting: GetAppSetting,
    private val updateAppSetting: UpdateAppSetting
) : BaseViewModel() {

    val versionName = BuildConfig.VERSION_NAME

    private val _isDarkMode = MutableLiveData(getAppSetting.getDarkMode())
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    private val _eventChangeDarkMode = MutableLiveData<Event<Boolean>>()
    val eventChangeDarkMode: LiveData<Event<Boolean>> get() = _eventChangeDarkMode

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    private val _eventStartUserPhotoActivity = MutableLiveData<Event<String>>()
    val eventStartUserPhotoActivity: LiveData<Event<String>> get() = _eventStartUserPhotoActivity

    private val _eventShowFollowerDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowerDialog: LiveData<Event<List<String>>> get() = _eventShowFollowerDialog

    private val _eventShowFollowingDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowingDialog: LiveData<Event<List<String>>> get() = _eventShowFollowingDialog

    private val _eventStartMyCatPhotos = MutableLiveData<Event<Unit>>()
    val eventStartMyCatPhotos: LiveData<Event<Unit>> get() = _eventStartMyCatPhotos

    private val _eventStartNotificationSetting = MutableLiveData<Event<Unit>>()
    val eventStartNotificationSetting: LiveData<Event<Unit>> get() = _eventStartNotificationSetting

    private val _eventShowGuide = MutableLiveData<Event<Unit>>()
    val eventShowGuide: LiveData<Event<Unit>> get() = _eventShowGuide

    private val _eventSharePosting = MutableLiveData<Event<String>>()
    val eventSharePosting: LiveData<Event<String>> get() = _eventSharePosting

    fun changeDarkMode() {
        val changedDarkMode = isDarkMode.value != true
        updateAppSetting.setDarkMode(changedDarkMode)
        _isDarkMode.postValue(changedDarkMode)

        _eventChangeDarkMode.emit(changedDarkMode)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun onClickPhotos(userId: String) {
        _eventStartUserPhotoActivity.emit(userId)
    }

    fun onClickFollowers(followerUserIds: List<String>) {
        _eventShowFollowerDialog.emit(followerUserIds)
    }

    fun onClickFollowing(followingUserIds: List<String>) {
        _eventShowFollowingDialog.emit(followingUserIds)
    }

    fun onClickMyCatPhotos() {
        _eventStartMyCatPhotos.emit()
    }

    fun onClickNotificationSetting() {
        _eventStartNotificationSetting.emit()
    }

    fun onClickGuide() {
        updateAppSetting.apply {
            setMainGuideStatus(true)
            setUploadGuideStatus(true)
        }
        _eventShowGuide.emit()
    }

    fun onClickShare() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deepLink = FirebaseDynamicLinkManager.SHARE_PHOTO_PREFIX

                handleResourceResult(
                    getDynamicLink(
                        deepLink = deepLink,
                        description = androidStringResourceManager.getString(R.string.cat_photo_sharing_sns),
                        imageUrl = FirebaseDynamicLinkManager.WOODY_CAT_IMAGE_URL
                    ),
                    onSuccess = { dynamicLink ->
                        _eventSharePosting.emit(dynamicLink)
                    })
            }
        }
    }
}