package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.presentation.upload.item.UploadStatus
import com.woody.cat.holic.presentation.upload.item.UploadingPhoto
import com.woody.cat.holic.usecase.AddPosting
import com.woody.cat.holic.usecase.UploadPhoto
import kotlinx.coroutines.*
import java.io.File

class UploadViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val uploadPhoto: UploadPhoto,
    private val addPosting: AddPosting
) : BaseViewModel() {

    private val _eventSelectImage = MutableLiveData<Unit>()
    val eventSelectImage: LiveData<Unit> get() = _eventSelectImage

    private val _eventMoveToNextPreviewPage = MutableLiveData<Unit>()
    val eventMoveToNextPreviewPage: LiveData<Unit> get() = _eventMoveToNextPreviewPage

    private val _eventMoveToPrevPreviewPage = MutableLiveData<Unit>()
    val eventMoveToPrevPreviewPage: LiveData<Unit> get() = _eventMoveToPrevPreviewPage

    private val _eventMoveToTargetPreviewPage = MutableLiveData<Int>()
    val eventMoveToTargetPreviewPage: LiveData<Int> get() = _eventMoveToTargetPreviewPage

    private val _eventRemoveTargetPreviewPage = MutableLiveData<Int>()
    val eventRemoveTargetPreviewPage: LiveData<Int> get() = _eventRemoveTargetPreviewPage

    private val _eventCancel = MutableLiveData<Unit>()
    val eventCancel: LiveData<Unit> get() = _eventCancel

    private val _eventShowPostingSuccessToast = MutableLiveData<Unit>()
    val eventShowPostingSuccessToast: LiveData<Unit> get() = _eventShowPostingSuccessToast

    private val _eventShowPostingErrorToast = MutableLiveData<Unit>()
    val eventShowPostingErrorToast: LiveData<Unit> get() = _eventShowPostingErrorToast

    private val _isLeftArrowButtonVisible = MutableLiveData(false)
    val isLeftArrowButtonVisible: LiveData<Boolean> get() = _isLeftArrowButtonVisible

    private val _isRightArrowButtonVisible = MutableLiveData(false)
    val isRightArrowButtonVisible: LiveData<Boolean> get() = _isRightArrowButtonVisible

    private val _eventUpdatePostingButtonEnableStatus = MutableLiveData<Unit>()
    val eventUpdatePostingButtonEnableStatus: LiveData<Unit> get() = _eventUpdatePostingButtonEnableStatus

    private val _isUploadPostingButtonEnabled = MutableLiveData(false)
    val isUploadPostingButtonEnabled: LiveData<Boolean> get() = _isUploadPostingButtonEnabled

    private val _previewData = MutableLiveData<MutableList<UploadingPhoto>>(mutableListOf())
    val previewData: LiveData<MutableList<UploadingPhoto>> get() = _previewData

    fun addPreviewData(data: List<String>) {
        _previewData.value = data.map { UploadingPhoto(imageUri = it) }
            .toMutableList()
            .apply { addAll(_previewData.value!!) }

        uploadPhotos()
        _eventUpdatePostingButtonEnableStatus.postValue(Unit)
    }

    fun removePreviewData(position: Int) {
        previewData.value?.apply {
            get(position).uploadingJob?.cancel(CancellationException())
            removeAt(position)
        }
        _eventUpdatePostingButtonEnableStatus.postValue(Unit)
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

    fun onClickRetryUploadPhoto(position: Int) {
        val uploadingPhoto = previewData.value?.get(position) ?: return
        uploadingPhoto.uploadingJob = uploadSinglePhoto(uploadingPhoto)
    }

    fun onClickUploadPosting() {
        //TODO: Loading Progress
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = addPosting(previewData.value
                    ?.filter { it.imageDownloadUrl != null }
                    ?.map {
                        Posting(
                            firebaseUserManager.getCurrentUserId(),
                            it.imageDownloadUrl!!
                        )
                    }
                    ?: listOf())

                handleNetworkResult(result, onSuccess = {
                    _eventShowPostingSuccessToast.postValue(Unit)
                    _eventCancel.postValue(Unit)
                }, onError = {
                    _eventShowPostingErrorToast.postValue(Unit)
                })
            }
        }
    }

    fun changeArrowButtonStatus(currentPosition: Int, dataSize: Int) {
        _isLeftArrowButtonVisible.postValue(currentPosition != 0)
        _isRightArrowButtonVisible.postValue(dataSize != 0 && currentPosition < dataSize - 1)
    }

    private fun uploadPhotos() {
        previewData.value?.forEach breaker@{ uploadingPhoto ->
            if (uploadingPhoto.uploadStatus.value != UploadStatus.UPLOADING) {
                return@breaker
            }

            uploadingPhoto.uploadingJob = uploadSinglePhoto(uploadingPhoto)
        }
    }

    fun updatePostingButtonEnableStatus() {
        if (previewData.value?.size == 0) {
            _isUploadPostingButtonEnabled.postValue(false)
            return
        }

        previewData.value?.forEach {
            if (it.uploadStatus.value != UploadStatus.COMPLETE) {
                _isUploadPostingButtonEnabled.postValue(false)
                return
            }
        }
        _isUploadPostingButtonEnabled.postValue(true)
    }

    private fun uploadSinglePhoto(uploadingPhoto: UploadingPhoto): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadingPhoto.uploadStatus.postValue(UploadStatus.UPLOADING)

                val result = uploadPhoto(File(uploadingPhoto.imageUri)) { progress ->
                    uploadingPhoto.currentProgress.postValue(progress)
                }

                handleNetworkResult(result, onSuccess = {
                    uploadingPhoto.apply {
                        imageDownloadUrl = it.imageUrl
                        uploadStatus.postValue(UploadStatus.COMPLETE)
                    }
                    _eventUpdatePostingButtonEnableStatus.postValue(Unit)
                }, onError = {
                    uploadingPhoto.uploadStatus.postValue(UploadStatus.FAIL)
                    it.printStackTrace()
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        previewData.value?.forEach {
            it.uploadingJob?.cancel(CancellationException())
        }
    }
}