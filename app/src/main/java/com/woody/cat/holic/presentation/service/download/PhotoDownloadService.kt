package com.woody.cat.holic.presentation.service.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.usecase.photo.DownloadPhoto

class PhotoDownloadService : LifecycleService() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID_PHOTO_DOWNLOAD = "NOTIFICATION_CHANNEL_ID_PHOTO_DOWNLOAD"
        private const val NOTIFICATION_ID = 1234
        private const val KEY_IMAGE_DOWNLOAD_URL = "KEY_IMAGE_DOWNLOAD_URL"

        fun getIntent(context: Context, imageDownloadUrl: String): Intent {
            return Intent(context, PhotoDownloadService::class.java).apply {
                putExtra(KEY_IMAGE_DOWNLOAD_URL, imageDownloadUrl)
            }
        }
    }

    lateinit var viewModel: PhotoDownloadViewModel

    override fun onCreate() {
        super.onCreate()

        viewModel = PhotoDownloadViewModel(DownloadPhoto(CatHolicApplication.application.fileManager)).apply {
            eventShowToast.observeEvent(this@PhotoDownloadService, { stringId ->
                Toast.makeText(this@PhotoDownloadService, stringId, Toast.LENGTH_SHORT).show()
            })

            eventStopService.observeEvent(this@PhotoDownloadService, {
                stopSelf()
            })
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_PHOTO_DOWNLOAD)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_notification_cat)
            .setContentTitle(getString(R.string.downloading_cat_photo))
            .setContentIntent(null)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.photo_download)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_PHOTO_DOWNLOAD, name, importance)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val imageDownloadUrl = intent?.getStringExtra(KEY_IMAGE_DOWNLOAD_URL) ?: run {
            stopSelf()
            return START_NOT_STICKY
        }

        viewModel.startDownloadPhoto(imageDownloadUrl)

        return START_NOT_STICKY
    }
}