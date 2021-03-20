package com.woody.cat.holic.presentation.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel

class UserViewModel(private val firebaseUserManager: FirebaseUserManager) : BaseViewModel() {

    private val _eventGoogleSignIn = MutableLiveData<Unit>()
    val eventGoogleSignIn: LiveData<Unit> get() = _eventGoogleSignIn

    private val _eventSignOut = MutableLiveData<Unit>()
    val eventSignOut: LiveData<Unit> get() = _eventSignOut

    private val _isSignIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean> get() = _isSignIn

    private val _userData = MutableLiveData<User>()
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

    fun onClickGoogleSignIn() {
        _eventGoogleSignIn.postValue(Unit)
    }

    fun onClickSignOut() {
        _eventSignOut.postValue(Unit)
    }
}