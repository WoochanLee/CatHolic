package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.usecase.photo.DetectCatFromPhoto
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.posting.AddPosting
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject


class UploadViewModel @Inject constructor(
    private val refreshEventBus: RefreshEventBus,
    private val getCurrentUserId: GetCurrentUserId,
    private val detectCatFromPhoto: DetectCatFromPhoto,
    private val uploadPhoto: UploadPhoto,
    private val addPosting: AddPosting
) : BaseViewModel() {

    private val _eventSelectImage = MutableLiveData<Event<Int>>()
    val eventSelectImage: LiveData<Event<Int>> get() = _eventSelectImage

    private val _eventMoveToNextPreviewPage = MutableLiveData<Event<Unit>>()
    val eventMoveToNextPreviewPage: LiveData<Event<Unit>> get() = _eventMoveToNextPreviewPage

    private val _eventMoveToPrevPreviewPage = MutableLiveData<Event<Unit>>()
    val eventMoveToPrevPreviewPage: LiveData<Event<Unit>> get() = _eventMoveToPrevPreviewPage

    private val _eventMoveToTargetPreviewPage = MutableLiveData<Event<Int>>()
    val eventMoveToTargetPreviewPage: LiveData<Event<Int>> get() = _eventMoveToTargetPreviewPage

    private val _eventRemoveTargetPreviewPage = MutableLiveData<Event<Int>>()
    val eventRemoveTargetPreviewPage: LiveData<Event<Int>> get() = _eventRemoveTargetPreviewPage

    private val _eventFinish = MutableLiveData<Event<Unit>>()
    val eventFinish: LiveData<Event<Unit>> get() = _eventFinish

    private val _eventShowToast = MutableLiveData<Event<Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _isLeftArrowButtonVisible = MutableLiveData(false)
    val isLeftArrowButtonVisible: LiveData<Boolean> get() = _isLeftArrowButtonVisible

    private val _isRightArrowButtonVisible = MutableLiveData(false)
    val isRightArrowButtonVisible: LiveData<Boolean> get() = _isRightArrowButtonVisible

    private val _isUploadButtonEnabled = MutableLiveData(false)
    val isUploadButtonEnabled: LiveData<Boolean> get() = _isUploadButtonEnabled

    private val _isVisibleGuide = MutableLiveData(true)
    val isVisibleGuide: LiveData<Boolean> get() = _isVisibleGuide

    private val _previewData = MutableLiveData<MutableList<UploadItem>>(mutableListOf())
    val previewData: LiveData<MutableList<UploadItem>> get() = _previewData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

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
        _eventMoveToPrevPreviewPage.emit()
    }

    fun onClickRightArrow() {
        _eventMoveToNextPreviewPage.emit()
    }

    fun onClickSmallPreview(targetPage: Int) {
        _eventMoveToTargetPreviewPage.emit(targetPage)
    }

    fun onClickPreviewRemove(targetPage: Int) {
        _eventRemoveTargetPreviewPage.emit(targetPage)
    }

    fun onClickSelectImageButton() {
        previewData.value?.size?.let { currentSelectedImageCount ->
            if (currentSelectedImageCount >= UploadActivity.ALBUM_MAX_SELECT_COUNT) {
                _eventShowToast.emit(R.string.you_can_upload_up_to_10_photos_at_a_time)
            } else {
                _eventSelectImage.emit(currentSelectedImageCount)
            }
        }
    }

    fun onClickRetryUploadPhoto(position: Int) {
        val userId = getCurrentUserId() ?: return handleNotSignedInUser()

        val uploadingPhoto = previewData.value?.get(position) ?: return
        uploadingPhoto.uploadingJob = uploadSinglePhoto(userId, uploadingPhoto)
    }

    fun onClickUploadPosting() {
        val userId = getCurrentUserId() ?: return handleNotSignedInUser()

        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val uploadPostingItemList = previewData.value
                    ?.filter { it.imageDownloadUrl.isNotEmpty() }
                    ?.map { Posting(userId = userId, downloadUrl = it.imageDownloadUrl) }
                    ?: listOf()

                handleResourceResult(addPosting(userId, uploadPostingItemList), onSuccess = {
                    _eventShowToast.emit(R.string.success_to_posting)
                    _eventFinish.emit()
                    refreshEventBus.emitEvent(GlobalRefreshEvent.UploadPostingEvent)
                }, onError = {
                    when (it) {
                        is NotSignedInException -> handleNotSignedInUser()
                        else -> _eventShowToast.emit(R.string.fail_to_posting)
                    }
                }, onComplete = {
                    _isLoading.postValue(false)
                })
            }
        }
    }

    fun onClickCloseGuide() {
        _isVisibleGuide.postValue(false)
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
                        handleResourceResult(detectCatFromPhoto(uploadItem.imageUri), onSuccess = { isCatDetected ->
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

                val result = uploadPhoto.uploadCatPhoto(userId, File(uploadItem.imageUri)) { progress ->
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
        _eventShowToast.emit(R.string.need_to_sign_in)
        _eventFinish.emit()
    }

    override fun onCleared() {
        super.onCleared()
        previewData.value?.forEach {
            it.uploadingJob?.cancel(CancellationException())
        }
    }
}