package com.woody.cat.holic.data

import com.woody.cat.holic.domain.Photo

interface PhotoDataSource {
    suspend fun uploadPhoto(fileName: String, fileUri: String)

    suspend fun getPhotos(): List<Photo>
}