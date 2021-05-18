package com.woody.cat.holic.usecase.notification

import com.woody.cat.holic.data.NotificationRepository

class UpdateNotification(private val notificationRepository: NotificationRepository) {

    suspend fun setCheckedNotification(id: Int) = notificationRepository.setCheckedNotification(id)
}