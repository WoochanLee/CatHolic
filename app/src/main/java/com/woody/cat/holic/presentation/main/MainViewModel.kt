package com.woody.cat.holic.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.framework.base.BaseViewModel

class MainViewModel : BaseViewModel() {

    private val _eventStartUploadActivity = MutableLiveData<Unit>()
    val eventStartUploadActivity: LiveData<Unit>
        get() = _eventStartUploadActivity


    fun onClickUploadFab() {
        _eventStartUploadActivity.postValue(Unit)
    }
}