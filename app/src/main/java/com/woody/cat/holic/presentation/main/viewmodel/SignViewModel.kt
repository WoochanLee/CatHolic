package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.user.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel

class SignViewModel(private val firebaseUserManager: FirebaseUserManager) : BaseViewModel() {

    private val _eventStartMyCatPhotos = MutableLiveData<Unit>()
    val eventStartMyCatPhotos: LiveData<Unit> get() = _eventStartMyCatPhotos

    private val _eventSignIn = MutableLiveData<Unit>()
    val eventSignIn: LiveData<Unit> get() = _eventSignIn

    private val _eventSignOut = MutableLiveData<Unit>()
    val eventSignOut: LiveData<Unit> get() = _eventSignOut

    private val _eventSignInSuccess = MutableLiveData<Unit>()
    val eventSignInSuccess: LiveData<Unit> get() = _eventSignInSuccess

    private val _eventSignOutSuccess = MutableLiveData<Unit>()
    val eventSignOutSuccess: LiveData<Unit> get() = _eventSignOutSuccess

    private val _isSignIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean> get() = _isSignIn

    private val _userData = MutableLiveData<User>(firebaseUserManager.getCurrentUser())
    val userData: LiveData<User> get() = _userData

    fun refreshSignInStatus() {
        _isSignIn.postValue(firebaseUserManager.isSignedIn())
        if (firebaseUserManager.isSignedIn()) {
            refreshUserData()
        }
    }

    private fun refreshUserData() {
        _userData.postValue(firebaseUserManager.getCurrentUser())
    }

    fun onClickMyCatPhotos() {
        _eventStartMyCatPhotos.postValue(Unit)
    }

    fun onClickSignIn() {
        _eventSignIn.postValue(Unit)
    }

    fun onSignInSuccess() {
        _eventSignInSuccess.postValue(Unit)
    }

    fun onClickSignOut() {
        _eventSignOut.postValue(Unit)
    }

    fun onSignOutSuccess() {
        _eventSignOutSuccess.postValue(Unit)
    }
}