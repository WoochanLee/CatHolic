package com.woody.cat.holic.presentation.main.posting.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.framework.manager.AndroidStringResourceManager
import com.woody.cat.holic.framework.manager.FirebaseDynamicLinkManager
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.share.GetDynamicLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostingDetailViewModel @Inject constructor(
    private val androidStringResourceManager: AndroidStringResourceManager,
    private val getDynamicLink: GetDynamicLink
) : BaseViewModel() {

    lateinit var postingItem: PostingItem

    private val _eventShowCommentDialog = MutableLiveData<Event<Unit>>()
    val eventShowCommentDialog: LiveData<Event<Unit>> get() = _eventShowCommentDialog

    private val _eventSharePosting = MutableLiveData<Event<String>>()
    val eventSharePosting: LiveData<Event<String>> get() = _eventSharePosting

    private val _isMenuVisible = MutableLiveData(true)
    val isMenuVisible: LiveData<Boolean> get() = _isMenuVisible

    private val _isEnabledSwipe = MutableLiveData(true)
    val isEnabledSwipe: LiveData<Boolean> get() = _isEnabledSwipe

    fun onClickPostingDetailImage() {
        _isMenuVisible.postValue(isMenuVisible.value != true)
        _isEnabledSwipe.postValue(isEnabledSwipe.value != true)
    }

    fun onClickComment() {
        _eventShowCommentDialog.emit()
    }

    fun onClickShare(postingId: String, title: String, imageUrl: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val deepLink = "${FirebaseDynamicLinkManager.SHARE_PHOTO_PREFIX}?postingId=$postingId"

                handleResourceResult(
                    getDynamicLink(
                        deepLink = deepLink,
                        description = androidStringResourceManager.getString(R.string.s_cat_photo, title),
                        imageUrl = imageUrl
                    ),
                    onSuccess = { dynamicLink ->
                        _eventSharePosting.emit(dynamicLink)
                    })
            }
        }
    }
}