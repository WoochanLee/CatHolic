package com.woody.cat.holic.data

import com.woody.cat.holic.domain.Photo

interface PhotoRepository {

    suspend fun uploadPhoto(fileUri: String, onProgress: (Int) -> Unit): Photo

    suspend fun getPhotos(): List<Photo>
}