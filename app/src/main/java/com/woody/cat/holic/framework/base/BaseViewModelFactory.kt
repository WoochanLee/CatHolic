package com.woody.cat.holic.framework.base

import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication

abstract class BaseViewModelFactory: ViewModelProvider.Factory {

    private val application = CatHolicApplication.application
    protected val refreshEventBus = application.refreshEventBus
    protected val settingRepository = application.settingRepository
    protected val pushTokenRepository = application.pushTokenRepository
    protected val pushTokenGenerateRepository = application.pushTokenGenerateRepository
    protected val photoRepository = application.photoRepository
    protected val postingRepository = application.postingRepository
    protected val likePostingRepository = application.likePostingRepository
    protected val userRepository = application.userRepository
    protected val commentRepository = application.commentRepository
    protected val followRepository = application.followRepository
    protected val photoAnalyzer = application.photoAnalyzer
    protected val fileManager = application.fileManager
}