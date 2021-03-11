package com.woody.cat.holic.presentation.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.Interactors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(interactors: Interactors) : BaseViewModel(interactors) {

    private val _photosLiveData = MutableLiveData<List<Photo>>()
    val photosLiveData: LiveData<List<Photo>>
        get() = _photosLiveData

    fun getPhotos() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _photosLiveData.postValue(interactors.getPhotos())
            }
        }
    }
}