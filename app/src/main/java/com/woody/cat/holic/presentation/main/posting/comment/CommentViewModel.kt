package com.woody.cat.holic.presentation.main.posting.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.domain.Comment
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.framework.paging.CommentDataSource
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.usecase.posting.comment.AddComment
import com.woody.cat.holic.usecase.posting.comment.GetComments
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentViewModel @Inject constructor(
    private val getCurrentUserId: GetCurrentUserId,
    private val addComment: AddComment,
    private val getComments: GetComments,
    private val getUserProfile: GetUserProfile
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    lateinit var postingItem: PostingItem

    private val _eventRefreshData = MutableLiveData<Event<Unit>>()
    val eventRefreshData: LiveData<Event<Unit>> get() = _eventRefreshData

    private val _eventStartProfileActivity = MutableLiveData<Event<String>>()
    val eventStartProfileActivity: LiveData<Event<String>> get() = _eventStartProfileActivity

    private val _writingEmojiStr = MutableLiveData("")
    val writeEmojiStr: LiveData<String> get() = _writingEmojiStr

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isListEmpty = MutableLiveData<Boolean>()
    val isListEmpty: LiveData<Boolean> get() = _isListEmpty

    fun getCommentFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            CommentDataSource(
                postingItem.postingId,
                getComments,
                getUserProfile
            )
        }
    ).flow.cachedIn(viewModelScope)

    fun setLoading(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }

    fun initData() {
        _eventRefreshData.emit()
    }

    fun setWritingEmojiStr(str: String) {
        _writingEmojiStr.postValue(str)
    }

    fun setIsListEmpty(isListEmpty: Boolean) {
        _isListEmpty.postValue(isListEmpty)
    }

    fun onClickProfile(userId: String) {
        _eventStartProfileActivity.emit(userId)
    }

    fun onClickAddComment() {
        val currentUserId = getCurrentUserId() ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setLoading(true)

                val commentEmojis = writeEmojiStr.value ?: ""

                _writingEmojiStr.postValue("")

                val result = addComment(
                    Comment(
                        userId = currentUserId,
                        postingId = postingItem.postingId,
                        commentEmojis = commentEmojis
                    )
                )

                _writingEmojiStr.postValue("")

                setLoading(false)

                handleResourceResult(result, onSuccess = {
                    initData()
                    postingItem.commentCount.apply { postValue((value ?: 0) + 1) }
                    //_eventShowToast.emit("success to add comment")
                }, onError = {
                    //TODO: handle network
                    it.printStackTraceIfDebug()
                })
            }
        }
    }
}