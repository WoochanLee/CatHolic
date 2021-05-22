package com.woody.cat.holic.presentation.main.user.profile

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.user.UpdateFollowUser
import com.woody.cat.holic.usecase.user.UpdateUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val getAppSetting: GetAppSetting,
    private val uploadPhoto: UploadPhoto,
    private val updateUserProfile: UpdateUserProfile,
    private val updateFollowUser: UpdateFollowUser
) : BaseViewModel() {

    val isDarkMode = getAppSetting.getDarkMode()

    private val _eventSelectBackgroundPhoto = MutableLiveData<Event<Unit>>()
    val eventSelectBackgroundPhoto: LiveData<Event<Unit>> get() = _eventSelectBackgroundPhoto

    private val _eventSelectProfilePhoto = MutableLiveData<Event<Unit>>()
    val eventSelectProfilePhoto: LiveData<Event<Unit>> get() = _eventSelectProfilePhoto

    private val _eventShowEditDisplayNameDialog = MutableLiveData<Event<Unit>>()
    val eventShowEditDisplayNameDialog: LiveData<Event<Unit>> get() = _eventShowEditDisplayNameDialog

    private val _eventShowEditGreetingsDialog = MutableLiveData<Event<Unit>>()
    val eventShowEditGreetingsDialog: LiveData<Event<Unit>> get() = _eventShowEditGreetingsDialog

    private val _eventStartUserPhotoActivity = MutableLiveData<Event<String>>()
    val eventStartUserPhotoActivity: LiveData<Event<String>> get() = _eventStartUserPhotoActivity

    private val _eventShowFollowerDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowerDialog: LiveData<Event<List<String>>> get() = _eventShowFollowerDialog

    private val _eventShowFollowingDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowingDialog: LiveData<Event<List<String>>> get() = _eventShowFollowingDialog

    private val _eventFinishActivity = MutableLiveData<Event<Unit>>()
    val eventFinishActivity: LiveData<Event<Unit>> get() = _eventFinishActivity

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _eventShowUnfollowAlertDialog = MutableLiveData<Event<Pair<String, String>>>()
    val eventShowUnfollowAlertDialog: LiveData<Event<Pair<String, String>>> get() = _eventShowUnfollowAlertDialog

    private val _eventShowPhotoZoomDialog = MutableLiveData<Event<String>>()
    val eventShowPhotoZoomDialog: LiveData<Event<String>> get() = _eventShowPhotoZoomDialog

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    private val _isMyProfile = MutableLiveData(false)
    val isMyProfile: LiveData<Boolean> get() = _isMyProfile

    private val _isUserFollowed = MutableLiveData<Boolean>()
    val isUserFollowed: LiveData<Boolean> get() = _isUserFollowed

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getProfile(targetUserId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(getUserProfile(targetUserId), onSuccess = { targetUser ->
                    _userProfile.postValue(targetUser)
                    checkIsMyProfile(targetUser.userId)
                    refreshIsUserFollowed(targetUser)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                    _eventFinishActivity.emit()
                })
            }
        }
    }

    fun uploadUserBackgroundPhoto(imageUri: String) {
        val userId = getCurrentUserId() ?: return

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(uploadPhoto.uploadUserBackgroundPhoto(userId, File(imageUri)), onSuccess = {
                    updateUserBackgroundPhotoUrl(userId, it.imageUrl)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun uploadUserProfilePhoto(imageUri: String) {
        val userId = getCurrentUserId() ?: return

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(uploadPhoto.uploadUserProfilePhoto(userId, File(imageUri)), onSuccess = {
                    updateUserProfilePhotoUrl(userId, it.imageUrl)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun onClickEditUserBackgroundPhoto() {
        _eventSelectBackgroundPhoto.emit()
    }

    fun onClickEditUserProfilePhoto() {
        _eventSelectProfilePhoto.emit()
    }

    fun onClickEditUserDisplayName() {
        _eventShowEditDisplayNameDialog.emit()
    }

    fun onClickEditUserGreetings() {
        _eventShowEditGreetingsDialog.emit()
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

    fun onClickFollowButton() {
        val myUserId = getCurrentUserId()
        val targetUserId = userProfile.value?.userId
        if (myUserId == null) {
            _eventShowToast.emit(R.string.need_to_sign_in)
            return
        }

        if (targetUserId == null) {
            _eventShowToast.emit(R.string.something_went_wrong)
            return
        }

        val currentUserFollowed = isUserFollowed.value == true

        if (currentUserFollowed) {
            _eventShowUnfollowAlertDialog.emit(Pair(myUserId, targetUserId))
            return
        } else {
            followUser(myUserId, targetUserId)
        }
    }

    fun onClickProfilePhoto(imageUrl: String) {
        _eventShowPhotoZoomDialog.emit(imageUrl)
    }

    private fun followUser(myUserId: String, targetUserId: String) {
        _isUserFollowed.postValue(isUserFollowed.value != true)

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateFollowUser.followUser(myUserId, targetUserId), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.FOLLOW_USER_EVENT)
                    getProfile(targetUserId)
                }, onError = {
                    _eventShowToast.emit(R.string.something_went_wrong)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun unfollowUser(myUserId: String, targetUserId: String) {
        _isUserFollowed.postValue(isUserFollowed.value != true)

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateFollowUser.unfollowUser(myUserId, targetUserId), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.FOLLOW_USER_EVENT)
                    getProfile(targetUserId)
                }, onError = {
                    _eventShowToast.emit(R.string.something_went_wrong)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    private fun refreshIsUserFollowed(targetUser: User) {
        val myUserId = getCurrentUserId()

        _isUserFollowed.postValue(targetUser.followerUserIds.contains(myUserId))
    }

    private fun updateUserBackgroundPhotoUrl(userId: String, imageUrl: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateUserProfile.updateUserBackgroundPhotoUrl(userId, imageUrl), onSuccess = {
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    private fun updateUserProfilePhotoUrl(userId: String, imageUrl: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateUserProfile.updateUserProfilePhotoUrl(userId, imageUrl), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.UPDATE_USER_PROFILE_EVENT)
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun updateUserDisplayName(displayName: String) {
        val userId = getCurrentUserId() ?: return

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateUserProfile.updateDisplayName(userId, displayName), onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.UPDATE_USER_PROFILE_EVENT)
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun updateUserGreetings(greetings: String) {
        val userId = getCurrentUserId() ?: return

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(updateUserProfile.updateGreetings(userId, greetings), onSuccess = {
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    private fun checkIsMyProfile(userId: String) {
        val currentUserId = getCurrentUserId()
        if (currentUserId == userId) {
            _isMyProfile.postValue(true)
        }
    }
}