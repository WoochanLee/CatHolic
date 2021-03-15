package com.woody.cat.holic.presentation.upload.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.usecase.UploadPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadImageViewModel(private val uploadPhoto: UploadPhoto) : BaseViewModel() {

    private val _totalImageCount = MutableLiveData<Int>()
    val totalImageCount: LiveData<Int> get() = _totalImageCount

    private val _currentImageCount = MutableLiveData(1)
    val currentImageCount: LiveData<Int> get() = _currentImageCount

    private val _currentProgress = MutableLiveData(0)
    val currentProgress: LiveData<Int> get() = _currentProgress

    private val _eventUploadComplete = MutableLiveData(Unit)
    val eventUploadComplete: LiveData<Unit> get() = _eventUploadComplete

    private fun setCurrentProgress(progress: Int) {
        _currentProgress.postValue(
            when {
                progress <= 0 -> 0
                progress >= 100 -> 100
                else -> progress
            }
        )
    }

    fun uploadPhotos(photoList: ArrayList<String>) {
        _totalImageCount.postValue(photoList.size)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                photoList.forEachIndexed { index, fileUri ->
                    uploadPhoto(fileUri) { progress ->
                        setCurrentProgress(progress)
                    }
                    _currentImageCount.postValue(index + 1)
                }
                _eventUploadComplete.postValue(Unit)
            }
        }
    }
}