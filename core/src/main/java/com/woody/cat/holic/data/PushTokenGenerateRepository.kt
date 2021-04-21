package com.woody.cat.holic.data

interface PushTokenGenerateRepository {

    suspend fun generatePushToken(): String?
}