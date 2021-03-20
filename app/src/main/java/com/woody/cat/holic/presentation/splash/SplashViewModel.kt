package com.woody.cat.holic.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.framework.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : BaseViewModel() {

    private val _eventStartMainActivity = MutableLiveData<Unit>()
    val eventStartMainActivity: LiveData<Unit> get() = _eventStartMainActivity

    init {
        delayStartMainActivity()
    }

    private fun delayStartMainActivity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(3000)
                _eventStartMainActivity.postValue(Unit)
            }
        }
    }
}