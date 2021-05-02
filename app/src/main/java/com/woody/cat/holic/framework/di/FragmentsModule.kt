package com.woody.cat.holic.framework.di

import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.UserFragment
import com.woody.cat.holic.presentation.main.user.profile.follower.FollowerListDialog
import com.woody.cat.holic.presentation.main.user.profile.following.FollowingListDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract fun contributeGalleryFragment(): GalleryFragment

    @ContributesAndroidInjector
    abstract fun contributeLikeFragment(): LikeFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeUFollowerListDialog(): FollowerListDialog

    @ContributesAndroidInjector
    abstract fun contributeCommentDialog(): CommentDialog

    @ContributesAndroidInjector
    abstract fun contributePostingDetailDialog(): PostingDetailDialog

    @ContributesAndroidInjector
    abstract fun contributeFollowingListDialog(): FollowingListDialog

    @ContributesAndroidInjector
    abstract fun contributeLikeListDialog(): LikeListDialog
}