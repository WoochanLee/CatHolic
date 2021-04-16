package com.woody.cat.holic.presentation.main.user.profile

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

class ProfileViewModel(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val getAppSetting: GetAppSetting,
    private val uploadPhoto: UploadPhoto,
    private val updateUserProfile: UpdateUserProfile,
    private val updateFollowUser: UpdateFollowUser,
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

    private val _eventStartPostingActivity = MutableLiveData<Event<String>>()
    val eventStartPostingActivity: LiveData<Event<String>> get() = _eventStartPostingActivity

    private val _eventShowFollowerDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowerDialog: LiveData<Event<List<String>>> get() = _eventShowFollowerDialog

    private val _eventShowFollowingDialog = MutableLiveData<Event<List<String>>>()
    val eventShowFollowingDialog: LiveData<Event<List<String>>> get() = _eventShowFollowingDialog

    private val _eventFinishActivity = MutableLiveData<Event<Unit>>()
    val eventFinishActivity: LiveData<Event<Unit>> get() = _eventFinishActivity

    private val _eventShowToast = MutableLiveData<Event<Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    private val _isMyProfile = MutableLiveData(false)
    val isMyProfile: LiveData<Boolean> get() = _isMyProfile

    private val _isUserFollowed = MutableLiveData<Boolean>()
    val isUserFollowed: LiveData<Boolean> get() = _isUserFollowed

    fun getProfile(targetUserId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getUserProfile(targetUserId)

                handleResourceResult(result, onSuccess = { targetUser ->
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

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = uploadPhoto.uploadUserBackgroundPhoto(userId, File(imageUri))

                handleResourceResult(result, onSuccess = {
                    updateUserBackgroundPhotoUrl(userId, it.imageUrl)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    fun uploadUserProfilePhoto(imageUri: String) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = uploadPhoto.uploadUserProfilePhoto(userId, File(imageUri))

                handleResourceResult(result, onSuccess = {
                    updateUserProfilePhotoUrl(userId, it.imageUrl)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
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
        _eventStartPostingActivity.emit(userId)
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

        _isUserFollowed.postValue(isUserFollowed.value != true)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = if (currentUserFollowed) {
                    updateFollowUser.unfollowUser(myUserId, targetUserId)
                } else {
                    updateFollowUser.followUser(myUserId, targetUserId)
                }

                handleResourceResult(result, onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.FollowUserEvent)
                    getProfile(targetUserId)
                }, onError = {
                    _eventShowToast.emit(R.string.something_went_wrong)
                })
            }
        }
    }

    private fun refreshIsUserFollowed(targetUser: User) {
        val myUserId = getCurrentUserId()

        _isUserFollowed.postValue(targetUser.followerUserIds.contains(myUserId))
    }

    private fun updateUserBackgroundPhotoUrl(userId: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = updateUserProfile.updateUserBackgroundPhotoUrl(userId, imageUrl)

                handleResourceResult(result, onSuccess = {
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    private fun updateUserProfilePhotoUrl(userId: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = updateUserProfile.updateUserProfilePhotoUrl(userId, imageUrl)

                handleResourceResult(result, onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.UpdateUserProfileEvent)
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    fun updateUserDisplayName(displayName: String) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = updateUserProfile.updateDisplayName(userId, displayName)

                handleResourceResult(result, onSuccess = {
                    refreshEventBus.emitEvent(GlobalRefreshEvent.UpdateUserProfileEvent)
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                })
            }
        }
    }

    fun updateUserGreetings(greetings: String) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = updateUserProfile.updateGreetings(userId, greetings)

                handleResourceResult(result, onSuccess = {
                    getProfile(userId)
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
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