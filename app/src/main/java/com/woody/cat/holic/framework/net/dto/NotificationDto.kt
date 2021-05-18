package com.woody.cat.holic.framework.net.dto

import com.woody.cat.holic.data.common.NotificationType
import com.woody.cat.holic.domain.Notification
import java.util.*


data class NotificationDto(
    val name: String,
    val body: String,
    val deepLink: String?,
    val notificationType: NotificationType
)

fun NotificationDto.mapToNotification(title: String): Notification {
    return Notification(
        created = Date().time,
        notificationType = notificationType,
        title = title,
        body = body,
        deepLink = deepLink
    )
}