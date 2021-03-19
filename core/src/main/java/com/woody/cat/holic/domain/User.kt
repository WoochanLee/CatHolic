package com.woody.cat.holic.domain

data class User(
    val userId: String,
    val displayName: String,
    val userPhotoUrl: String? = null,
)