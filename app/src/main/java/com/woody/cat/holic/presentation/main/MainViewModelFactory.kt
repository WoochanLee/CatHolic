package com.woody.cat.holic.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.CatHolicApplication
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.usecase.AddLikeInPosting
import com.woody.cat.holic.usecase.GetNextNormalPostings
import com.woody.cat.holic.usecase.RemoveLikeInPosting

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                FirebaseUserManager,
                GetNextNormalPostings(CatHolicApplication.application.postingRepository),
                AddLikeInPosting(CatHolicApplication.application.postingRepository),
                RemoveLikeInPosting(CatHolicApplication.application.postingRepository)
            ) as T
        } else {
            throw IllegalStateException()
        }
    }
}