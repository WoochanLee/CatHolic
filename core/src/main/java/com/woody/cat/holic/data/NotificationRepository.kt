package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Notification

interface NotificationRepository {

    suspend fun addNotification(notification: Notification): Resource<Unit>

    suspend fun getNotifications(pageSize: Int, offset: Int): Resource<List<Notification>>

    suspend fun deleteAllNotifications(): Resource<Unit>

    suspend fun setCheckedNotification(id: Int): Resource<Unit>
}