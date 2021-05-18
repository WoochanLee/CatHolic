package com.woody.cat.holic.framework.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.woody.cat.holic.framework.db.model.NotificationEntity

@Dao
interface NotificationDao {

    @Insert
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    @Query("SELECT * FROM notification ORDER BY created DESC LIMIT :pageSize OFFSET :offset")
    suspend fun getNotifications(pageSize: Int, offset: Int): List<NotificationEntity>

    @Query("DELETE FROM notification")
    suspend fun deleteAllNotifications()

    @Query("UPDATE notification SET isChecked = :isChecked WHERE id = :id")
    suspend fun setIsCheckedById(id: Int, isChecked: Boolean)
}