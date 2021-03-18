package com.woody.cat.holic.domain

data class Posting(
    val userId: String,
    val downloadUrl: String,
    val stared: Int = 0,
    val reported: Int = 0,
    val created: String? = null,
    val updated: String? = null
)