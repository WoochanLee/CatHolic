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

    private val _eventMoveToNextPreviewPage = MutableLiveData(Unit)
    val eventMoveToNextPreviewPage: LiveData<Unit> get() = _eventMoveToNextPreviewPage

    private val _eventMoveToPrevPreviewPage = MutableLiveData(Unit)
    val eventMoveToPrevPreviewPage: LiveData<Unit> get() = _eventMoveToPrevPreviewPage

    private val _eventMoveToTargetPreviewPage = MutableLiveData<Int>()
    val eventMoveToTargetPreviewPage: LiveData<Int> get() = _eventMoveToTargetPreviewPage

    private val _eventRemoveTargetPreviewPage = MutableLiveData<Int>()
    val eventRemoveTargetPreviewPage: LiveData<Int> get() = _eventRemoveTargetPreviewPage

    private val _eventCancel = MutableLiveData<Unit>()
    val eventCancel: LiveData<Unit> get() = _eventCancel

    private val _isLeftArrowButtonVisible = MutableLiveData(false)
    val isLeftArrowButtonVisible: LiveData<Boolean> get() = _isLeftArrowButtonVisible

    private val _isRightArrowButtonVisible = MutableLiveData(false)
    val isRightArrowButtonVisible: LiveData<Boolean> get() = _isRightArrowButtonVisible


    var previewData = mutableListOf<String>()

    fun refreshPreviewData(data: List<String>) {
        previewData = data.toMutableList()
    }

    fun removePreviewData(position: Int) {
        previewData.removeAt(position)
    }

    fun onClickUpload() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //addPostings()
            }
        }
    }

    fun onClickLeftArrow() {
        _eventMoveToPrevPreviewPage.postValue(Unit)
    }

    fun onClickRightArrow() {
        _eventMoveToNextPreviewPage.postValue(Unit)
    }

    fun onClickSmallPreview(targetPage: Int) {
        _eventMoveToTargetPreviewPage.postValue(targetPage)
    }

    fun onClickSmallPreviewRemove(targetPage: Int) {
        _eventRemoveTargetPreviewPage.postValue(targetPage)
    }

    fun onClickSelectImageButton() {
        _eventSelectImage.postValue(Unit)
    }

    fun onClickCancel() {
        _eventCancel.postValue(Unit)
    }

    fun checkAndChangeArrowButtonStatus(currentPosition: Int, dataSize: Int) {
        _isLeftArrowButtonVisible.postValue(currentPosition != 0)
        _isRightArrowButtonVisible.postValue(currentPosition != dataSize - 1)
    }
}