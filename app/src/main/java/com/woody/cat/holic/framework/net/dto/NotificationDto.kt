package com.woody.cat.holic.framework.net.dto

import com.woody.cat.holic.presentation.service.fcm.NotificationType

data class NotificationDto(
    val title: String,
    val body: String,
    val deepLink: String,
    val notificationType: NotificationType
)