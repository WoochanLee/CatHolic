package com.woody.cat.holic.presentation.service.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.woody.cat.holic.R
import com.woody.cat.holic.data.common.NotificationType
import com.woody.cat.holic.framework.net.dto.NotificationDto
import com.woody.cat.holic.framework.net.dto.mapToNotification
import dagger.android.AndroidInjection
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CatHolicFirebaseMessagingService @Inject constructor() : FirebaseMessagingService() {

    companion object {
        const val DEEP_LINK_QUERY_POSTING_ID = "postingId"
        const val DEEP_LINK_QUERY_COMMENT_ID = "commentId"
        const val DEEP_LINK_QUERY_USER_ID = "userId"

        @Volatile
        private var notificationId = 0
            @Synchronized get() {
                return field++
            }
    }

    @Inject
    lateinit var viewModel: CatHolicFirebaseMessagingViewModel

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data

        if (data.isNotEmpty()) {

            val notificationDto = Gson().run { fromJson(toJson(data), NotificationDto::class.java) }

            val title = when(notificationDto.notificationType) {
                NotificationType.NOTIFICATION_CHANNEL_ID_FOLLOW -> R.string.s_just_follow_you
                NotificationType.NOTIFICATION_CHANNEL_ID_POSTING -> R.string.s_just_posted_a_new_cat_photo
                NotificationType.NOTIFICATION_CHANNEL_ID_COMMENT -> R.string.s_just_commented_on_your_photo
                NotificationType.NOTIFICATION_CHANNEL_ID_LIKE -> R.string.s_just_liked_your_photo
            }.let { titleResId ->
                getString(titleResId, notificationDto.name)
            }

            viewModel.addNotificationToLocalDB(notificationDto.mapToNotification(title))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationDto.notificationType)
            }

            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                `package` = packageName
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                notificationDto.deepLink?.let { deepLink ->
                    setData(Uri.parse(deepLink))
                }
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val androidNotification = NotificationCompat.Builder(this, notificationDto.notificationType.channelId)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification_cat)
                .setContentTitle(title)
                .setContentText(getString(R.string.to_check_it_right_away_touch_this_notification))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(this).notify(notificationId, androidNotification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationType: NotificationType) {
        val name = when(notificationType) {
            NotificationType.NOTIFICATION_CHANNEL_ID_FOLLOW -> getString(R.string.new_follower)
            NotificationType.NOTIFICATION_CHANNEL_ID_POSTING -> getString(R.string.new_posting_from_you_follow)
            NotificationType.NOTIFICATION_CHANNEL_ID_COMMENT -> getString(R.string.new_comment)
            NotificationType.NOTIFICATION_CHANNEL_ID_LIKE -> getString(R.string.new_like)
        }
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(notificationType.channelId, name, importance)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}