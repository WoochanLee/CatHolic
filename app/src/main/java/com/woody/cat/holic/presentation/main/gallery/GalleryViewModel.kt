package com.woody.cat.holic.presentation.main.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.usecase.GetPhotos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(private val getPhotos: GetPhotos) : BaseViewModel() {

    private val _photosLiveData = MutableLiveData<List<Photo>>()
    val photosLiveData: LiveData<List<Photo>> get() = _photosLiveData

    fun initPhotos() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _photosLiveData.postValue(getPhotos())
                } catch (e: Exception) {
                    //TODO: network error handle
                }
            }
        }
    }
}