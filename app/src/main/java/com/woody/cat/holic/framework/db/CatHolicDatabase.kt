package com.woody.cat.holic.framework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.woody.cat.holic.framework.db.dao.NotificationDao
import com.woody.cat.holic.framework.db.model.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 3)
abstract class CatHolicDatabase: RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "catholic.db"

        @Volatile
        private var instance: CatHolicDatabase? = null

        private fun create(context: Context): CatHolicDatabase {
            return Room.databaseBuilder(context, CatHolicDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getInstance(context: Context): CatHolicDatabase = (instance ?: create(context).also { instance = it })
    }

    abstract fun notificationDao(): NotificationDao
}