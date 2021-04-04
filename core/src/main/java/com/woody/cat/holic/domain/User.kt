package com.woody.cat.holic.domain

data class User(
    val userId: String,
    var displayName: String? = null,
    var userPhotoUrl: String? = null,
)