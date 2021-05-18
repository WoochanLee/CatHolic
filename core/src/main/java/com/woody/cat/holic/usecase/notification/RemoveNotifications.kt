package com.woody.cat.holic.usecase.notification

import com.woody.cat.holic.data.NotificationRepository

class RemoveNotifications(private val notificationRepository: NotificationRepository) {

    suspend operator fun invoke() = notificationRepository.deleteAllNotifications()
}