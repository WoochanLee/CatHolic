package com.woody.cat.holic.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor() : BaseViewModel() {

    private val _eventStartMainActivity = MutableLiveData<Event<Unit>>()
    val eventStartMainActivity: LiveData<Event<Unit>> get() = _eventStartMainActivity

    init {
        delayStartMainActivity()
    }

    private fun delayStartMainActivity() {
        viewModelScope.launch {
            delay(1000)
            _eventStartMainActivity.emit()
        }
    }
}