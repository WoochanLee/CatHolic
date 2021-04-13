package com.woody.cat.holic.framework.base

import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication

abstract class BaseViewModelFactory: ViewModelProvider.Factory {

    private val application = CatHolicApplication.application
    protected val refreshEventBus = application.refreshEventBus
    protected val settingRepository = application.settingRepository
    protected val photoRepository = application.photoRepository
    protected val postingRepository = application.postingRepository
    protected val userRepository = application.userRepository
    protected val commentRepository = application.commentRepository
    protected val photoAnalyzer = application.photoAnalyzer
}