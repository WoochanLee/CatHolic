package com.woody.cat.holic.framework.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.woody.cat.holic.data.common.NotificationType
import com.woody.cat.holic.domain.Notification

@Entity(tableName = "notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isChecked: Boolean,
    val created: Long,
    val notificationType: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String?
)

fun Notification.mapToNotificationEntity(): NotificationEntity {
    return NotificationEntity(
        isChecked = isChecked,
        created = created,
        notificationType = notificationType,
        title = title,
        body = body,
        deepLink = deepLink
    )
}

fun NotificationEntity.mapToNotification(): Notification {
    return Notification(
        id = id,
        isChecked = isChecked,
        created = created,
        notificationType = notificationType,
        title = title,
        body = body,
        deepLink = deepLink
    )
}