package com.woody.cat.holic.framework.db

import android.content.Context
import com.woody.cat.holic.data.NotificationRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Notification
import com.woody.cat.holic.framework.db.model.mapToNotification
import com.woody.cat.holic.framework.db.model.mapToNotificationEntity

class RoomNotificationRepository(context: Context) : NotificationRepository {

    private val notificationDao = CatHolicDatabase.getInstance(context).notificationDao()

    override suspend fun addNotification(notification: Notification): Resource<Unit> {
        return try {
            notificationDao.insertNotification(notification.mapToNotificationEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getNotifications(pageSize: Int, offset: Int): Resource<List<Notification>> {
        return try {
            Resource.Success(notificationDao.getNotifications(pageSize, offset).map { it.mapToNotification() })
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteAllNotifications(): Resource<Unit> {
        return try {
            Resource.Success(notificationDao.deleteAllNotifications())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun setCheckedNotification(id: Int): Resource<Unit> {
        return try {
            Resource.Success(notificationDao.setIsCheckedById(id, true))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}