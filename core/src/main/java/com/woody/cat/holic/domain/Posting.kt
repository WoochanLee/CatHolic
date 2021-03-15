package com.woody.cat.holic.domain

data class Posting(
    val downloadUrl: String,
    val staredCount: Int = 0,
    val reportedCount: Int = 0
)