package com.woody.cat.holic.framework.di

import com.woody.cat.holic.data.*
import com.woody.cat.holic.usecase.config.GetForceUpdateVersion
import com.woody.cat.holic.usecase.config.GetIsServiceAvailable
import com.woody.cat.holic.usecase.config.RefreshRemoteConfig
import com.woody.cat.holic.usecase.notification.AddNotification
import com.woody.cat.holic.usecase.notification.GetNotifications
import com.woody.cat.holic.usecase.notification.RemoveNotifications
import com.woody.cat.holic.usecase.notification.UpdateNotification
import com.woody.cat.holic.usecase.photo.DetectCatFromPhoto
import com.woody.cat.holic.usecase.photo.DownloadPhoto
import com.woody.cat.holic.usecase.photo.UploadPhoto
import com.woody.cat.holic.usecase.posting.*
import com.woody.cat.holic.usecase.posting.comment.AddComment
import com.woody.cat.holic.usecase.posting.comment.GetComments
import com.woody.cat.holic.usecase.setting.GetAppSetting
import com.woody.cat.holic.usecase.setting.GetPushToken
import com.woody.cat.holic.usecase.setting.UpdateAppSetting
import com.woody.cat.holic.usecase.share.GetDynamicLink
import com.woody.cat.holic.usecase.user.*
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideDetectCatFromPhoto(photoAnalyzer: PhotoAnalyzer): DetectCatFromPhoto {
        return DetectCatFromPhoto(photoAnalyzer)
    }

    @Provides
    fun provideDownloadPhoto(fileManager: FileManager): DownloadPhoto {
        return DownloadPhoto(fileManager)
    }

    @Provides
    fun provideUploadPhoto(photoRepository: PhotoRepository): UploadPhoto {
        return UploadPhoto(photoRepository)
    }

    @Provides
    fun provideAddComment(commentRepository: CommentRepository): AddComment {
        return AddComment(commentRepository)
    }

    @Provides
    fun provideGetComments(commentRepository: CommentRepository): GetComments {
        return GetComments(commentRepository)
    }

    @Provides
    fun provideAddPosting(postingRepository: PostingRepository): AddPosting {
        return AddPosting(postingRepository)
    }

    @Provides
    fun provideChangeToNextPostingOrder(postingRepository: PostingRepository): ChangeToNextPostingOrder {
        return ChangeToNextPostingOrder(postingRepository)
    }

    @Provides
    fun provideGetGalleryPostings(postingRepository: PostingRepository): GetGalleryPostings {
        return GetGalleryPostings(postingRepository)
    }

    @Provides
    fun provideGetPostingOrder(postingRepository: PostingRepository): GetPostingOrder {
        return GetPostingOrder(postingRepository)
    }

    @Provides
    fun provideGetUserLikePostings(postingRepository: PostingRepository): GetUserLikePostings {
        return GetUserLikePostings(postingRepository)
    }

    @Provides
    fun provideGetUserPostings(postingRepository: PostingRepository): GetUserPostings {
        return GetUserPostings(postingRepository)
    }

    @Provides
    fun provideGetUserUploadedPostings(postingRepository: PostingRepository): GetUserUploadedPostings {
        return GetUserUploadedPostings(postingRepository)
    }

    @Provides
    fun provideRemoveUserPosting(postingRepository: PostingRepository): RemoveUserPosting {
        return RemoveUserPosting(postingRepository)
    }

    @Provides
    fun provideUpdateLikedPosting(likePostingRepository: LikePostingRepository): UpdateLikedPosting {
        return UpdateLikedPosting(likePostingRepository)
    }

    @Provides
    fun provideGetAppSetting(settingRepository: SettingRepository): GetAppSetting {
        return GetAppSetting(settingRepository)
    }

    @Provides
    fun provideGetPushToken(pushTokenGenerateRepository: PushTokenGenerateRepository): GetPushToken {
        return GetPushToken(pushTokenGenerateRepository)
    }

    @Provides
    fun provideUpdateSetting(settingRepository: SettingRepository): UpdateAppSetting {
        return UpdateAppSetting(settingRepository)
    }

    @Provides
    fun provideAddPushToken(pushTokenRepository: PushTokenRepository): AddPushToken {
        return AddPushToken(pushTokenRepository)
    }

    @Provides
    fun provideAddUserProfile(userRepository: UserRepository): AddUserProfile {
        return AddUserProfile(userRepository)
    }

    @Provides
    fun provideGetCurrentUserId(userRepository: UserRepository): GetCurrentUserId {
        return GetCurrentUserId(userRepository)
    }

    @Provides
    fun provideGetIsSignedIn(userRepository: UserRepository): GetIsSignedIn {
        return GetIsSignedIn(userRepository)
    }

    @Provides
    fun provideGetUserProfile(userRepository: UserRepository): GetUserProfile {
        return GetUserProfile(userRepository)
    }

    @Provides
    fun provideRemovePushToken(pushTokenRepository: PushTokenRepository): RemovePushToken {
        return RemovePushToken(pushTokenRepository)
    }

    @Provides
    fun provideUpdateFollowUser(followRepository: FollowRepository): UpdateFollowUser {
        return UpdateFollowUser(followRepository)
    }

    @Provides
    fun provideUpdateUserProfile(userRepository: UserRepository): UpdateUserProfile {
        return UpdateUserProfile(userRepository)
    }

    @Provides
    fun provideGetSinglePosting(postingRepository: PostingRepository): GetSinglePosting {
        return GetSinglePosting(postingRepository)
    }

    @Provides
    fun provideGetDynamicLink(dynamicLinkManager: DynamicLinkManager): GetDynamicLink {
        return GetDynamicLink(dynamicLinkManager)
    }

    @Provides
    fun provideAddNotification(notificationRepository: NotificationRepository): AddNotification {
        return AddNotification(notificationRepository)
    }

    @Provides
    fun provideGetNotifications(notificationRepository: NotificationRepository): GetNotifications {
        return GetNotifications(notificationRepository)
    }

    @Provides
    fun provideRemoveNotifications(notificationRepository: NotificationRepository): RemoveNotifications {
        return RemoveNotifications(notificationRepository)
    }

    @Provides
    fun provideUpdateNotification(notificationRepository: NotificationRepository): UpdateNotification {
        return UpdateNotification(notificationRepository)
    }

    @Provides
    fun provideGetRemoteConfig(remoteConfigRepository: RemoteConfigRepository): RefreshRemoteConfig {
        return RefreshRemoteConfig(remoteConfigRepository)
    }

    @Provides
    fun provideGetIsServiceAvailable(remoteConfigRepository: RemoteConfigRepository): GetIsServiceAvailable {
        return GetIsServiceAvailable(remoteConfigRepository)
    }

    @Provides
    fun provideGetForceUpdateVersion(remoteConfigRepository: RemoteConfigRepository): GetForceUpdateVersion {
        return GetForceUpdateVersion(remoteConfigRepository)
    }
}