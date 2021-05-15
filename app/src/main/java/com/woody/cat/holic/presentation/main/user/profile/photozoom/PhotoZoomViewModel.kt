package com.woody.cat.holic.presentation.main.user.profile.photozoom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woody.cat.holic.framework.base.BaseViewModel
import javax.inject.Inject

class PhotoZoomViewModel @Inject constructor() : BaseViewModel() {

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    fun initImageUrl(imageUrl: String) {
        _imageUrl.postValue(imageUrl)
    }
}