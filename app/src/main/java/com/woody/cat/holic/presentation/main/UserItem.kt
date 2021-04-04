package com.woody.cat.holic.presentation.main

import androidx.lifecycle.MutableLiveData

data class UserItem(
    val userId: String,
    val displayName: MutableLiveData<String?> = MutableLiveData(),
    val userPhotoUrl: MutableLiveData<String?> = MutableLiveData(),
)