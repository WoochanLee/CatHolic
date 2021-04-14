package com.woody.cat.holic.presentation.main.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val uploadPhoto: UploadPhoto
) : BaseViewModel() {

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

    private fun checkIsMyProfile(userId: String) {
        val currentUserId = getCurrentUserId()
        if (currentUserId == userId) {
            _isMyProfile.postValue(true)
        }
    }
}