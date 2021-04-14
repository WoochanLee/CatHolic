package com.woody.cat.holic.framework.paging.item

import androidx.lifecycle.MutableLiveData

data class UserItem(
    val userId: String,
    val displayName: MutableLiveData<String?> = MutableLiveData(),
    val userPhotoUrl: MutableLiveData<String?> = MutableLiveData()
)