package com.woody.cat.holic.presentation.main.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.Event
import com.woody.cat.holic.framework.base.emit
import com.woody.cat.holic.framework.paging.NotificationDataSource
import com.woody.cat.holic.usecase.notification.GetNotifications
import com.woody.cat.holic.usecase.notification.UpdateNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationListViewModel @Inject constructor(
    private val getNotifications: GetNotifications,
    private val updateNotification: UpdateNotification
) : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10
    }

    private val _eventHandleDeepLink = MutableLiveData<Event<String>>()
    val eventHandleDeepLink: LiveData<Event<String>> get() = _eventHandleDeepLink

    fun getNotificationFlow() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            NotificationDataSource(getNotifications)
        }
    ).flow.cachedIn(viewModelScope)

    fun onClickNotification(id: Int, deepLink: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                updateNotification.setCheckedNotification(id)
            }
        }
        _eventHandleDeepLink.emit(deepLink)
    }
}