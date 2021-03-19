package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.upload.item.UploadStatus
import com.woody.cat.holic.presentation.upload.item.UploadingPhotoItem
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

    private val _eventShowPostingToast = MutableLiveData<Int>()
    val eventShowToast: LiveData<Int> get() = _eventShowPostingToast

    private val _isLeftArrowButtonVisible = MutableLiveData(false)
    val isLeftArrowButtonVisible: LiveData<Boolean> get() = _isLeftArrowButtonVisible

    private val _isRightArrowButtonVisible = MutableLiveData(false)
    val isRightArrowButtonVisible: LiveData<Boolean> get() = _isRightArrowButtonVisible

    private val _eventUpdatePostingButtonEnableStatus = MutableLiveData<Unit>()
    val eventUpdatePostingButtonEnableStatus: LiveData<Unit> get() = _eventUpdatePostingButtonEnableStatus

    private val _isUploadPostingButtonEnabled = MutableLiveData(false)
    val isUploadPostingButtonEnabled: LiveData<Boolean> get() = _isUploadPostingButtonEnabled

    private val _previewData = MutableLiveData<MutableList<UploadingPhotoItem>>(mutableListOf())
    val previewData: LiveData<MutableList<UploadingPhotoItem>> get() = _previewData

    fun addPreviewData(data: List<String>) {
        _previewData.value = data.map { UploadingPhotoItem(imageUri = it) }
            .toMutableList()
            .apply { addAll(_previewData.value ?: emptyList()) }

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

        val user = firebaseUserManager.getCurrentUser()

        if (user == null) {
            handleNotSignedInUser()
            return
        }

        //TODO: Loading Progress
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val uploadPostingItemList = previewData.value
                    ?.filter { it.imageDownloadUrl.isNotEmpty() }
                    ?.map {
                        Posting(
                            user,
                            it.imageDownloadUrl
                        )
                    }
                    ?: listOf()

                val result = addPosting(uploadPostingItemList)

                handleNetworkResult(result, onSuccess = {
                    _eventShowPostingToast.postValue(R.string.success_to_posting)
                    _eventCancel.postValue(Unit)
                }, onError = {
                    when (it) {
                        is NotSignedInException -> handleNotSignedInUser()
                        else -> _eventShowPostingToast.postValue(R.string.fail_to_posting)
                    }
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

    private fun uploadSinglePhoto(uploadingPhotoItem: UploadingPhotoItem): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadingPhotoItem.uploadStatus.postValue(UploadStatus.UPLOADING)

                val result = uploadPhoto(File(uploadingPhotoItem.imageUri)) { progress ->
                    uploadingPhotoItem.currentProgress.postValue(progress)
                }

                handleNetworkResult(result, onSuccess = {
                    uploadingPhotoItem.apply {
                        imageDownloadUrl = it.imageUrl
                        uploadStatus.postValue(UploadStatus.COMPLETE)
                    }
                    _eventUpdatePostingButtonEnableStatus.postValue(Unit)
                }, onError = {
                    when (it) {
                        is NotSignedInException -> handleNotSignedInUser()
                        else -> uploadingPhotoItem.uploadStatus.postValue(UploadStatus.FAIL)
                    }
                })
            }
        }
    }

    private fun handleNotSignedInUser() {
        _eventShowPostingToast.postValue(R.string.need_to_sign_in)
        _eventCancel.postValue(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        previewData.value?.forEach {
            it.uploadingJob?.cancel(CancellationException())
        }
    }
}