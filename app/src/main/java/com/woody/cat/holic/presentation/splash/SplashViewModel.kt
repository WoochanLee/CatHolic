package com.woody.cat.holic.presentation.splash

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.BuildConfig
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.usecase.config.GetForceUpdateVersion
import com.woody.cat.holic.usecase.config.GetIsServiceAvailable
import com.woody.cat.holic.usecase.config.RefreshRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val refreshRemoteConfig: RefreshRemoteConfig,
    private val getIsServiceAvailable: GetIsServiceAvailable,
    private val getForceUpdateVersion: GetForceUpdateVersion
) : BaseViewModel() {

    private val _eventStartMainActivity = MutableLiveData<Event<Unit>>()
    val eventStartMainActivity: LiveData<Event<Unit>> get() = _eventStartMainActivity

    private val _eventServiceNotAvailableDialog = MutableLiveData<Event<Unit>>()
    val eventServiceNotAvailableDialog: LiveData<Event<Unit>> get() = _eventServiceNotAvailableDialog

    private val _eventShowToast = MutableLiveData<Event<@StringRes Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _eventFinishActivity = MutableLiveData<Event<Unit>>()
    val eventFinishActivity: LiveData<Event<Unit>> get() = _eventFinishActivity

    private val _eventForceUpdate = MutableLiveData<Event<Unit>>()
    val eventForceUpdate: LiveData<Event<Unit>> get() = _eventForceUpdate

    init {
        checkRemoteConfig()
    }

    private fun checkRemoteConfig() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(refreshRemoteConfig(), onSuccess = {
                    if (checkIsServiceAvailable()) {
                        checkForceUpdateVersion()
                    }
                }, onError = {
                    _eventShowToast.emit(R.string.network_fail)
                    _eventFinishActivity.emit()
                })
            }
        }
    }

    private fun checkIsServiceAvailable(): Boolean {
        return (getIsServiceAvailable() || BuildConfig.DEBUG).also { isServiceAvailable ->
            if (!isServiceAvailable) {
                _eventServiceNotAvailableDialog.emit()
            }
        }
    }

    private fun checkForceUpdateVersion() {
        if (getForceUpdateVersion() > BuildConfig.VERSION_CODE) {
            _eventForceUpdate.emit()
        } else {
            _eventStartMainActivity.emit()
        }
    }
}