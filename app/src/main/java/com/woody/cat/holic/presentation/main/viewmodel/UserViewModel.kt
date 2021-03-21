package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel

class UserViewModel(private val firebaseUserManager: FirebaseUserManager) : BaseViewModel() {

    private val _eventGoogleSignIn = MutableLiveData<Unit>()
    val eventGoogleSignIn: LiveData<Unit> get() = _eventGoogleSignIn

    private val _eventSignInSuccess = MutableLiveData<Unit>()
    val eventSignInSuccess: LiveData<Unit> get() = _eventSignInSuccess

    private val _eventSignOutSuccess = MutableLiveData<Unit>()
    val eventSignOutSuccess: LiveData<Unit> get() = _eventSignOutSuccess

    private val _isSignIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean> get() = _isSignIn

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun onSignInSuccess() {
        _eventSignInSuccess.postValue(Unit)
    }

    fun refreshSignInStatus() {
        _isSignIn.postValue(firebaseUserManager.isSignedIn())
        if (firebaseUserManager.isSignedIn()) {
            refreshUserData()
        }
    }

    private fun refreshUserData() {
        _userData.postValue(firebaseUserManager.getCurrentUser())
    }

    fun onClickGoogleSignIn() {
        _eventGoogleSignIn.postValue(Unit)
    }

    fun onClickSignOut() {
        _eventSignOutSuccess.postValue(Unit)
    }
}