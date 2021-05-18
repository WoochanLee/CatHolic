package com.woody.cat.holic.usecase.notification

import com.woody.cat.holic.data.NotificationRepository
import com.woody.cat.holic.domain.Notification

class AddNotification(private val notificationRepository: NotificationRepository) {

    suspend operator fun invoke(notification: Notification) = notificationRepository.addNotification(notification)
}