package com.woody.cat.holic.framework.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.data.*
import com.woody.cat.holic.framework.db.RoomNotificationRepository
import com.woody.cat.holic.framework.db.SharedPreferenceSettingRepository
import com.woody.cat.holic.framework.manager.AndroidFileManager
import com.woody.cat.holic.framework.manager.AndroidStringResourceManager
import com.woody.cat.holic.framework.manager.FirebaseDynamicLinkManager
import com.woody.cat.holic.framework.manager.GoogleMLPhotoAnalyzer
import com.woody.cat.holic.framework.net.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class RepositoryModule {

    companion object {
        private const val NAME_SETTING_REPOSITORY = "SETTING_REPOSITORY"
    }

    @Provides
    @Singleton
    @Inject
    fun provideSharedPreferences(context: Context) = context.getSharedPreferences(NAME_SETTING_REPOSITORY, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseMessaging() = FirebaseMessaging.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorageReference() = FirebaseStorage.getInstance().apply {
        maxDownloadRetryTimeMillis = FirebaseStoragePhotoRepository.MAX_RETRY_TIME_MILLIS
        maxUploadRetryTimeMillis = FirebaseStoragePhotoRepository.MAX_RETRY_TIME_MILLIS
    }.reference

    @Provides
    @Singleton
    fun provideSettingRepository(db: SharedPreferences): SettingRepository = SharedPreferenceSettingRepository(db)

    @Provides
    @Singleton
    fun providePushTokenRepository(db: FirebaseFirestore): PushTokenRepository = FirebaseFirestorePushTokenRepository(db)

    @Provides
    @Singleton
    fun provideCommentRepository(db: FirebaseFirestore): CommentRepository = FirebaseFirestoreCommentRepository(db)

    @Provides
    @Singleton
    fun providePostingRepository(db: FirebaseFirestore): PostingRepository = FirebaseFirestorePostingRepository(db)

    @Provides
    @Singleton
    fun provideLikePostingRepository(db: FirebaseFirestore): LikePostingRepository = FirebaseFirestoreLikePostingRepository(db)

    @Provides
    @Singleton
    fun provideFollowRepository(db: FirebaseFirestore): FollowRepository = FirebaseFirestoreFollowRepository(db)

    @Provides
    @Singleton
    fun provideUserRepository(db: FirebaseFirestore, auth: FirebaseAuth): UserRepository = FirebaseFirestoreUserRepository(db, auth)

    @Provides
    @Singleton
    fun providePushTokenGenerateRepository(db: FirebaseMessaging): PushTokenGenerateRepository = FirebaseMessagingPushTokenGenerateRepository(db)

    @Provides
    @Singleton
    fun providePhotoRepository(db: StorageReference): PhotoRepository = FirebaseStoragePhotoRepository(db)

    @Provides
    @Singleton
    fun provideNotificationRepository(context: Context): NotificationRepository = RoomNotificationRepository(context)

    @Provides
    @Singleton
    fun providePhotoAnalyzer(context: Context): PhotoAnalyzer = GoogleMLPhotoAnalyzer(context)

    @Provides
    @Singleton
    fun provideFileManager(context: Context): FileManager = AndroidFileManager(context)

    @Provides
    @Singleton
    fun provideDynamicLinkManager(): DynamicLinkManager = FirebaseDynamicLinkManager()

    @Provides
    @Singleton
    fun provideAndroidStringResourceManager(context: Context): AndroidStringResourceManager = AndroidStringResourceManager(context)
}