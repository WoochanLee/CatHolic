package com.woody.cat.holic.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.usecase.GetNextPostings

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(FirebaseUserManager, GetNextPostings(CatHolicApplication.application.postingRepository)) as T
        } else {
            throw IllegalStateException()
        }
    }
}