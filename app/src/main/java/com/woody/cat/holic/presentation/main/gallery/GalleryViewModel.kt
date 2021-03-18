package com.woody.cat.holic.presentation.main.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.usecase.GetNextPostings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(
    private val firebaseUserManager: FirebaseUserManager,
    private val getNextPostings: GetNextPostings
) : BaseViewModel() {

    companion object {
        const val POSTING_PAGE_SIZE = 10
    }

    private val _photosLiveData = MutableLiveData<List<Photo>>()
    val photosLiveData: LiveData<List<Photo>> get() = _photosLiveData

    fun initPhotos() {
        //TODO: OrderBy
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getNextPostings(POSTING_PAGE_SIZE, PostingOrder.CREATED)

                handleNetworkResult(result, onSuccess = { postingList ->
                    _photosLiveData.postValue(postingList.map {
                        Photo(firebaseUserManager.getCurrentUserId(), it.downloadUrl)
                    })
                }, onError = {
                    it.printStackTrace()
                })
            }
        }
    }
}
