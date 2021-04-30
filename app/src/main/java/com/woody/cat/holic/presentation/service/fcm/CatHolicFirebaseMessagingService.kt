package com.woody.cat.holic.presentation.service.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.net.dto.NotificationDto
import com.woody.cat.holic.presentation.splash.SplashActivity

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CatHolicFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID_FOLLOW = "NOTIFICATION_CHANNEL_ID_FOLLOW"
        const val NOTIFICATION_CHANNEL_ID_POSTING = "NOTIFICATION_CHANNEL_ID_POSTING"
        const val NOTIFICATION_CHANNEL_ID_COMMENT = "NOTIFICATION_CHANNEL_ID_COMMENT"
        const val NOTIFICATION_CHANNEL_ID_LIKE = "NOTIFICATION_CHANNEL_ID_LIKE"

        @Volatile
        private var notificationId = 0
            @Synchronized get() {
                return field++
            }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data

        if (data.isNotEmpty()) {

            val notificationDto = Gson().run { fromJson(toJson(data), NotificationDto::class.java) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationDto.notificationType)
            }

            val intent = Intent(this, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val notification = NotificationCompat.Builder(this, notificationDto.notificationType.channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification_cat)
                .setContentTitle(notificationDto.title)
                .setContentText(notificationDto.body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(this).notify(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationType: NotificationType) {
        val name = getString(notificationType.notificationName)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(notificationType.channelId, name, importance)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

enum class NotificationType(
    val channelId: String,

    @StringRes
    val notificationName: Int
) {
    NOTIFICATION_CHANNEL_ID_FOLLOW(CatHolicFirebaseMessagingService.NOTIFICATION_CHANNEL_ID_FOLLOW, R.string.new_follower),
    NOTIFICATION_CHANNEL_ID_POSTING(CatHolicFirebaseMessagingService.NOTIFICATION_CHANNEL_ID_POSTING, R.string.new_posting_from_you_follow),
    NOTIFICATION_CHANNEL_ID_COMMENT(CatHolicFirebaseMessagingService.NOTIFICATION_CHANNEL_ID_COMMENT, R.string.new_comment),
    NOTIFICATION_CHANNEL_ID_LIKE(CatHolicFirebaseMessagingService.NOTIFICATION_CHANNEL_ID_LIKE, R.string.new_like),
}