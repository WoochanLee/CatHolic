package com.woody.cat.holic.presentation.upload.item

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job

data class UploadingPhoto(
    val userId: String? = null,
    val imageUri: String,
    var imageDownloadUrl: String? = null,
    var currentProgress: MutableLiveData<Int> = MutableLiveData(0),
    val uploadStatus: MutableLiveData<UploadStatus> = MutableLiveData(UploadStatus.UPLOADING),
    var uploadingJob: Job? = null
)

enum class UploadStatus{
    UPLOADING,
    COMPLETE,
    FAIL
}