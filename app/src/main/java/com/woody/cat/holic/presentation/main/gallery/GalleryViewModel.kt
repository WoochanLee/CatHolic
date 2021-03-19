package com.woody.cat.holic.presentation.main.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.data.PostingOrder
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleNetworkResult
import com.woody.cat.holic.usecase.GetNextPostings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(private val getNextPostings: GetNextPostings) : BaseViewModel() {

    companion object {
        const val POSTING_PAGE_SIZE = 10
    }

    private val _postingsLiveData = MutableLiveData<List<Posting>>()
    val postingsLiveData: LiveData<List<Posting>> get() = _postingsLiveData

    fun initPostings() {
        //TODO: OrderBy
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = getNextPostings(POSTING_PAGE_SIZE, PostingOrder.CREATED)

                handleNetworkResult(result, onSuccess = { postingList ->
                    _postingsLiveData.postValue(postingList)
                }, onError = {
                    //TODO: handle network error
                })
            }
        }
    }
}
