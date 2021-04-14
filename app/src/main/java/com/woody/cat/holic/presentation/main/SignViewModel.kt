package com.woody.cat.holic.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.net.common.DataNotExistException
import com.woody.cat.holic.usecase.user.AddUserProfile
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetIsSignedIn
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignViewModel(
    private val refreshEventBus: RefreshEventBus,
    private val firebaseAuth: FirebaseAuth,
    private val getIsSignedIn: GetIsSignedIn,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val addUserProfile: AddUserProfile,
) : BaseViewModel() {

    lateinit var gso: GoogleSignInOptions

    private val _eventStartMyCatPhotos = MutableLiveData<Event<Unit>>()
    val eventStartMyCatPhotos: LiveData<Event<Unit>> get() = _eventStartMyCatPhotos

    private val _eventSignIn = MutableLiveData<Event<Unit>>()
    val eventSignIn: LiveData<Event<Unit>> get() = _eventSignIn

    private val _eventSignOut = MutableLiveData<Event<Unit>>()
    val eventSignOut: LiveData<Event<Unit>> get() = _eventSignOut

    private val _eventSignInSuccess = MutableLiveData<Event<Unit>>()
    val eventSignInSuccess: LiveData<Event<Unit>> get() = _eventSignInSuccess

    private val _eventSignOutSuccess = MutableLiveData<Event<Unit>>()
    val eventSignOutSuccess: LiveData<Event<Unit>> get() = _eventSignOutSuccess

    private val _eventSignInFail = MutableLiveData<Event<Unit>>()
    val eventSignInFail: LiveData<Event<Unit>> get() = _eventSignInFail

    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignIn: LiveData<Boolean> get() = _isSignedIn

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    init {
        initEventBusSubscribe()
    }

    fun initFirebaseAuth(clientId: String) {
        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .build()
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        _isLoading.postValue(true)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    _isLoading.postValue(false)
                    CatHolicLogger.log("success to firebase google sign in")
                    val user = it.user?.run { User(uid, displayName ?: "Unknown", photoUrl?.toString() ?: "") }
                    if (user != null) {
                        getProfileOrMakeProfile(user)
                    } else {
                        _eventSignInFail.emit()
                    }
                }
                .addOnFailureListener {
                    _isLoading.postValue(false)
                    CatHolicLogger.log("fail to firebase google sign in")
                    _eventSignInFail.emit()
                }
        } catch (e: ApiException) {
            _isLoading.postValue(false)
            if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                CatHolicLogger.log("cancel to firebase google sign in")
            } else {
                CatHolicLogger.log("fail to firebase google sign in")
                _eventSignInFail.emit()
            }
        }
    }

    private fun getProfileOrMakeProfile(user: User) {
        getProfile(user.userId, onResult = {
            if (it != null) {
                _isSignedIn.postValue(getIsSignedIn())
                _userData.postValue(it)
                _eventSignInSuccess.emit()
            } else {
                makeProfile(user)
            }
        }, onError = {
            _eventSignOut.emit()
        })
    }

    private fun getProfile(userId: String, onResult: (User?) -> Unit, onError: ((Exception) -> Unit)? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                val result = getUserProfile(userId)
                _isLoading.postValue(false)
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
                _isLoading.postValue(true)
                val result = addUserProfile(user)
                _isLoading.postValue(false)
                handleResourceResult(result, onSuccess = {
                    getProfileOrMakeProfile(user)
                }, onError = {
                    _eventSignInFail.emit()
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
            _eventSignOut.emit()
        }
    }

    fun onClickMyCatPhotos() {
        _eventStartMyCatPhotos.emit()
    }

    fun onClickSignIn() {
        _eventSignIn.emit()
    }

    fun onClickSignOut() {
        _eventSignOut.emit()
    }

    fun onSignOutSuccess() {
        _eventSignOutSuccess.emit()
    }

    fun signOutFirebase() {
        firebaseAuth.signOut()
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.UploadPostingEvent,
                GlobalRefreshEvent.DeletePostingEvent
            ) {
                refreshSignInStatus()
            }
        }
    }
}