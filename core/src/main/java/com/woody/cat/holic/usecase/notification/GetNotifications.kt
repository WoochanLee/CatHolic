package com.woody.cat.holic.usecase.notification

import com.woody.cat.holic.data.NotificationRepository

class GetNotifications(private val notificationRepository: NotificationRepository) {

    suspend operator fun invoke(pageSize: Int, offset: Int) = notificationRepository.getNotifications(pageSize, offset)
}