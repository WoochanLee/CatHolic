package com.woody.cat.holic.presentation.service.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.usecase.photo.DownloadPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoDownloadViewModel @Inject constructor(val downloadPhoto: DownloadPhoto) : BaseViewModel() {

    private val _eventShowToast = MutableLiveData<Event<Int>>()
    val eventShowToast: LiveData<Event<Int>> get() = _eventShowToast

    private val _eventStopService = MutableLiveData<Event<Unit>>()
    val eventStopService: LiveData<Event<Unit>> get() = _eventStopService

    fun startDownloadPhoto(imageUrl: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                _eventShowToast.emit(R.string.start_to_download_photo)
                handleResourceResult(downloadPhoto(imageUrl), onSuccess = {
                    _eventShowToast.emit(R.string.success_to_download_photo)
                }, onError = {
                    _eventShowToast.emit(R.string.fail_to_download_photo)
                }, onComplete = {
                    _eventStopService.emit()
                })
            }
        }
    }
}