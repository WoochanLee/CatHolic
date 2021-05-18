package com.woody.cat.holic.framework.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.presentation.main.MainViewModel
import com.woody.cat.holic.presentation.main.SignViewModel
import com.woody.cat.holic.presentation.main.follow.FollowViewModel
import com.woody.cat.holic.presentation.main.gallery.GalleryViewModel
import com.woody.cat.holic.presentation.main.like.LikeViewModel
import com.woody.cat.holic.presentation.main.notification.NotificationListViewModel
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.comment.CommentViewModel
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailViewModel
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListViewModel
import com.woody.cat.holic.presentation.main.user.UserViewModel
import com.woody.cat.holic.presentation.main.user.myphoto.MyPhotoViewModel
import com.woody.cat.holic.presentation.main.user.profile.ProfileViewModel
import com.woody.cat.holic.presentation.main.user.profile.follower.FollowerListViewModel
import com.woody.cat.holic.presentation.main.user.profile.following.FollowingListViewModel
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoViewModel
import com.woody.cat.holic.presentation.main.user.profile.photozoom.PhotoZoomViewModel
import com.woody.cat.holic.presentation.splash.SplashViewModel
import com.woody.cat.holic.presentation.upload.UploadViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    abstract fun provideGalleryViewModel(galleryViewModel: GalleryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LikeViewModel::class)
    abstract fun provideLikeViewModel(likeViewModel: LikeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun provideUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignViewModel::class)
    abstract fun provideSignViewModel(signViewModel: SignViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostingViewModel::class)
    abstract fun providePostingViewModel(postingViewModel: PostingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommentViewModel::class)
    abstract fun provideCommentViewModel(commentViewModel: CommentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostingDetailViewModel::class)
    abstract fun providePostingDetailViewModel(postingDetailViewModel: PostingDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LikeListViewModel::class)
    abstract fun provideLikeListViewModel(likeListViewModel: LikeListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPhotoViewModel::class)
    abstract fun provideMyPhotoViewModel(myPhotoViewModel: MyPhotoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowerListViewModel::class)
    abstract fun provideFollowerListViewModel(followerListViewModel: FollowerListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowingListViewModel::class)
    abstract fun provideFollowingListViewModel(followingListViewModel: FollowingListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserPhotoViewModel::class)
    abstract fun provideUserPhotoViewModel(userPhotoViewModel: UserPhotoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun provideSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UploadViewModel::class)
    abstract fun provideUploadViewModel(uploadViewModel: UploadViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FollowViewModel::class)
    abstract fun provideFollowViewModel(followViewModel: FollowViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhotoZoomViewModel::class)
    abstract fun providePhotoZoomViewModel(photoZoomViewModel: PhotoZoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationListViewModel::class)
    abstract fun provideNotificationListViewModel(notificationListViewModel: NotificationListViewModel): ViewModel

    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
