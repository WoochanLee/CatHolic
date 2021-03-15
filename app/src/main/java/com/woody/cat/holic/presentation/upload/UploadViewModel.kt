package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.usecase.AddPostings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadViewModel(private val addPostings: AddPostings) : BaseViewModel() {

    private val _eventSelectImage = MutableLiveData<Unit>()
    val eventSelectImage: LiveData<Unit> get() = _eventSelectImage

    //val albumFiles = arrayListOf<AlbumFile>()

    fun onClickUpload() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //addPostings()
            }
        }
    }

    fun onClickSelectImage() {
        _eventSelectImage.postValue(Unit)
    }
}