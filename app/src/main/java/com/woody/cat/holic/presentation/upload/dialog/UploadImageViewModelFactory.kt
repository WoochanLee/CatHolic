package com.woody.cat.holic.presentation.upload.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.usecase.UploadPhoto

class UploadImageViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadImageViewModel::class.java)) {
            return UploadImageViewModel(UploadPhoto(CatHolicApplication.application.photoRepository)) as T
        } else {
            throw IllegalStateException()
        }
    }
}