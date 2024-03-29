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
import com.woody.cat.holic.usecase.notification.RemoveNotifications
import com.woody.cat.holic.usecase.setting.GetPushToken
import com.woody.cat.holic.usecase.user.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val firebaseAuth: FirebaseAuth,
    private val getIsSignedIn: GetIsSignedIn,
    private val getCurrentUserId: GetCurrentUserId,
    private val getUserProfile: GetUserProfile,
    private val addUserProfile: AddUserProfile,
    private val getPushToken: GetPushToken,
    private val addPushToken: AddPushToken,
    private val removePushToken: RemovePushToken,
    private val removeNotifications: RemoveNotifications
) : BaseViewModel() {

    lateinit var gso: GoogleSignInOptions

    private val _eventSignIn = MutableLiveData<Event<Unit>>()
    val eventSignIn: LiveData<Event<Unit>> get() = _eventSignIn

    private val _eventSignOut = MutableLiveData<Event<Unit>>()
    val eventSignOut: LiveData<Event<Unit>> get() = _eventSignOut

    private val _eventSignInFail = MutableLiveData<Event<Unit>>()
    val eventSignInFail: LiveData<Event<Unit>> get() = _eventSignInFail

    private val _eventBlockedUserAndFinish = MutableLiveData<Event<Unit>>()
    val eventBlockedUserAndFinish: LiveData<Event<Unit>> get() = _eventBlockedUserAndFinish

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
                refreshEventBus.emitEvent(GlobalRefreshEvent.SIGN_IN_STATUS_CHANGE_EVENT)
                addUserPushToken(user.userId)
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
                handleResourceResult(result, onSuccess = { user ->
                    checkBlockedAccount(user)
                    onResult(user)
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

    private fun checkBlockedAccount(user: User) {
        if (user.blocked) {
            _eventBlockedUserAndFinish.emit()
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

    private fun addUserPushToken(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val pushToken = getPushToken() ?: return@withContext
                addPushToken(userId, pushToken)
            }
        }
    }

    private fun removeUserPushToken(userId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(removePushToken(userId), onComplete = onComplete)
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

    fun onClickSignIn() {
        _eventSignIn.emit()
    }

    fun onClickSignOut() {
        _eventSignOut.emit()
    }

    fun signOutFirebase() {
        removeUserPushToken(userData.value?.userId ?: return, onComplete = {
            firebaseAuth.signOut()
            refreshSignInStatus()
            refreshEventBus.emitEvent(GlobalRefreshEvent.SIGN_IN_STATUS_CHANGE_EVENT)
        })
    }

    fun removeUserNotifications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                removeNotifications()
            }
        }
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            refreshEventBus.subscribeEvent(
                GlobalRefreshEvent.UPLOAD_POSTING_EVENT,
                GlobalRefreshEvent.DELETE_POSTING_EVENT,
                GlobalRefreshEvent.FOLLOW_USER_EVENT,
                GlobalRefreshEvent.UPDATE_USER_PROFILE_EVENT,
                GlobalRefreshEvent.SIGN_IN_STATUS_CHANGE_EVENT
            ) {
                refreshSignInStatus()
            }
        }
    }
}