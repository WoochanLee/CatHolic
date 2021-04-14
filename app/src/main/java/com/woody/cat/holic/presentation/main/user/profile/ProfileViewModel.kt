package com.woody.cat.holic.presentation.main.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import com.woody.cat.holic.usecase.user.UpdateUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val uploadPhoto: UploadPhoto,
    private val updateUserProfile: UpdateUserProfile
) : BaseViewModel() {

    private val _eventSelectBackgroundPhoto = MutableLiveData<Event<Unit>>()
    val eventSelectBackgroundPhoto: LiveData<Event<Unit>> get() = _eventSelectBackgroundPhoto

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    private val _isMyProfile = MutableLiveData(false)
    val isMyProfile: LiveData<Boolean> get() = _isMyProfile

    fun getProfile(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getUserProfile(userId)

                handleResourceResult(result, onSuccess = {
                    _userProfile.postValue(it)
                    checkIsMyProfile(it.userId)
                }, onError = {
                    //TODO: handle network error
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
                    //TODO: handle network error
                })
            }
        }
    }

    fun onClickUserBackgroundPhoto() {
        _eventSelectBackgroundPhoto.emit()
    }

    private fun updateUserBackgroundPhotoUrl(userId: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = updateUserProfile.updateBackgroundPhotoUrl(userId, imageUrl)

                handleResourceResult(result, onSuccess = {
                    getProfile(userId)
                }, onError = {
                    //TODO: handle network error
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