package com.woody.cat.holic.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.net.common.DataNotExistException
import com.woody.cat.holic.usecase.user.AddUserProfile
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val getIsSignedIn: GetIsSignedIn,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val addUserProfile: AddUserProfile,
) : BaseViewModel() {

    lateinit var gso: GoogleSignInOptions

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

    private val _eventSignInFail = MutableLiveData<Unit>()
    val eventSignInFail: LiveData<Unit> get() = _eventSignInFail

    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean> get() = _isSignedIn

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun initFirebaseAuth(clientId: String) {
        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .build()
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    CatHolicLogger.log("success to firebase google sign in")
                    val user = it.user?.run { User(uid, displayName ?: "Unknown", photoUrl?.toString() ?: "") }
                    if (user != null) {
                        getProfileOrMakeProfile(user)
                    } else {
                        _eventSignInFail.postValue(Unit)
                    }
                }
                .addOnFailureListener {
                    CatHolicLogger.log("fail to firebase google sign in")
                    _eventSignInFail.postValue(Unit)
                }
        } catch (e: ApiException) {
            CatHolicLogger.log("fail to firebase google sign in")
            _eventSignInFail.postValue(Unit)
        }
    }

    private fun getProfileOrMakeProfile(user: User) {
        getProfile(user.userId, onResult = {
            if (it != null) {
                _isSignedIn.postValue(getIsSignedIn())
                _userData.postValue(it)
                _eventSignInSuccess.postValue(Unit)
            } else {
                makeProfile(user)
            }
        }, onError = {
            _eventSignOut.postValue(Unit)
        })
    }

    private fun getProfile(userId: String, onResult: (User?) -> Unit, onError: ((Exception) -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getUserProfile(userId)

                handleResourceResult(result, onSuccess = {
                    onResult(it)
                }, onError = {
                    if (it is DataNotExistException) {
                        onResult(null)
                    } else {
                        onError?.invoke(it)
                    }
                })
            }
        }
    }

    private fun makeProfile(user: User) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = addUserProfile(user)

                handleResourceResult(result, onSuccess = {
                    getProfileOrMakeProfile(user)
                }, onError = {
                    _eventSignInFail.postValue(Unit)
                })
            }
        }
    }

    fun refreshSignInStatus() {
        _isSignedIn.postValue(getIsSignedIn())
        if (getIsSignedIn()) {
            refreshUserData()
        }
    }

    private fun refreshUserData() {
        val userId = getCurrentUserId()

        if (userId != null) {
            getProfile(userId, onResult = {
                it.let { user ->
                    _userData.postValue(user)
                }
            })
        } else {
            _eventSignOut.postValue(Unit)
        }
    }

    fun onClickMyCatPhotos() {
        _eventStartMyCatPhotos.postValue(Unit)
    }

    fun onClickSignIn() {
        _eventSignIn.postValue(Unit)
    }

    fun onClickSignOut() {
        _eventSignOut.postValue(Unit)
    }

    fun onSignOutSuccess() {
        _eventSignOutSuccess.postValue(Unit)
    }

    fun signOutFirbase() {
        firebaseAuth.signOut()
    }
}