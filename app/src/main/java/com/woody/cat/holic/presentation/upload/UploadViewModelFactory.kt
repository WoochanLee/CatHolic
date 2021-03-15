package com.woody.cat.holic.presentation.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.AddPostings

class UploadViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(AddPostings(CatHolicApplication.application.postingRepository)) as T
        } else {
            throw IllegalStateException()
        }
    }
}