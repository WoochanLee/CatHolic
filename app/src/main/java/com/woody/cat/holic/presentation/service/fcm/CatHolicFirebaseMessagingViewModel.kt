package com.woody.cat.holic.presentation.service.fcm

import com.woody.cat.holic.domain.Notification
import com.woody.cat.holic.framework.base.BaseViewModel
import com.woody.cat.holic.framework.base.handleResourceResult
import com.woody.cat.holic.usecase.notification.AddNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatHolicFirebaseMessagingViewModel @Inject constructor(private val addNotification: AddNotification) : BaseViewModel() {

    fun addNotificationToLocalDB(notification: Notification) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                handleResourceResult(addNotification(notification))
            }
        }
    }
}