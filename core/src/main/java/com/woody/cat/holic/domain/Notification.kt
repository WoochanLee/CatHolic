package com.woody.cat.holic.domain

import com.woody.cat.holic.data.common.NotificationType

data class Notification(
    val id: Int = -1,
    val isChecked: Boolean = false,
    val created: Long,
    val notificationType: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String?
)