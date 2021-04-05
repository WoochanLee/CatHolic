package com.woody.cat.holic.presentation.upload.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.upload.UploadItem
import com.woody.cat.holic.presentation.upload.UploadStatus
import com.woody.cat.holic.usecase.photo.DetectCatFromPhoto
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.posting.AddPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File


class UploadViewModel(
    private val getCurrentUserId: GetCurrentUserId,
    private val detectCatFromPhoto: DetectCatFromPhoto,
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

    private val _isUploadButtonEnabled = MutableLiveData(false)
    val isUploadButtonEnabled: LiveData<Boolean> get() = _isUploadButtonEnabled

    private val _previewData = MutableLiveData<MutableList<UploadItem>>(mutableListOf())
    val previewData: LiveData<MutableList<UploadItem>> get() = _previewData

    private val updatePostingButtonEnableStatus = flow {
        if (previewData.value?.size == 0) {
            return@flow emit(false)
        }

        previewData.value?.forEach {
            if (it.uploadStatus.value != UploadStatus.COMPLETE) {
                return@flow emit(false)
            }
        }
        emit(true)
    }

    fun addPreviewData(data: List<String>) {
        _previewData.value = data.map { UploadItem(imageUri = it) }
            .toMutableList()
            .apply { addAll(_previewData.value ?: emptyList()) }

        handleSelectedPhotos()

        refreshUpdatePostingButtonEnableStatus()
    }

    fun removePreviewData(position: Int) {
        previewData.value?.apply {
            get(position).uploadingJob?.cancel(CancellationException())
            _previewData.value = this.apply { removeAt(position) }
        }

        refreshUpdatePostingButtonEnableStatus()
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

    fun onClickPreviewRemove(targetPage: Int) {
        _eventRemoveTargetPreviewPage.postValue(targetPage)
    }

    fun onClickSelectImageButton() {
        _eventSelectImage.postValue(Unit)
    }

    fun onClickRetryUploadPhoto(position: Int) {

        val userId = getCurrentUserId() ?: return handleNotSignedInUser()

        val uploadingPhoto = previewData.value?.get(position) ?: return
        uploadingPhoto.uploadingJob = uploadSinglePhoto(userId, uploadingPhoto)
    }

    fun onClickUploadPosting() {

        val userId = getCurrentUserId() ?: return handleNotSignedInUser()

        //TODO: Loading Progress
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val uploadPostingItemList = previewData.value
                    ?.filter { it.imageDownloadUrl.isNotEmpty() }
                    ?.map { Posting(userId, it.imageDownloadUrl) }
                    ?: listOf()

                val result = addPosting(uploadPostingItemList)

                handleResourceResult(result, onSuccess = {
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

    private fun handleSelectedPhotos() {
        val userId = getCurrentUserId() ?: return handleNotSignedInUser()

        previewData.value
            ?.filter { it.uploadStatus.value == UploadStatus.READY }
            ?.forEach { uploadItem ->
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = detectCatFromPhoto(uploadItem.imageUri)

                        handleResourceResult(result, onSuccess = { isCatDetected ->
                            if (isCatDetected) {
                                uploadItem.uploadingJob = uploadSinglePhoto(userId, uploadItem)
                            } else {
                                uploadItem.uploadStatus.postValue(UploadStatus.CAT_NOT_DETECTED)
                            }
                        }, onError = {
                            uploadItem.uploadStatus.postValue(UploadStatus.CAT_NOT_DETECTED)
                        })
                    }
                }
            }
    }

    private fun uploadSinglePhoto(userId: String, uploadItem: UploadItem): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadItem.uploadStatus.postValue(UploadStatus.UPLOADING)

                val result = uploadPhoto(userId, File(uploadItem.imageUri)) { progress ->
                    uploadItem.currentProgress.postValue(progress)
                }

                handleResourceResult(result, onSuccess = {
                    uploadItem.apply {
                        imageDownloadUrl = it.imageUrl
                        uploadStatus.postValue(UploadStatus.COMPLETE)
                    }

                    refreshUpdatePostingButtonEnableStatus()
                }, onError = {
                    when (it) {
                        is NotSignedInException -> handleNotSignedInUser()
                        else -> uploadItem.uploadStatus.postValue(UploadStatus.FAIL)
                    }
                })
            }
        }
    }

    private fun refreshUpdatePostingButtonEnableStatus() {
        viewModelScope.launch {
            updatePostingButtonEnableStatus.collect { isEnabled ->
                _isUploadButtonEnabled.postValue(isEnabled)
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