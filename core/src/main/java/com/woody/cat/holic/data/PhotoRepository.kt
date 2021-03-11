package com.woody.cat.holic.data

class PhotoRepository(private val dataSource: PhotoDataSource) {
    suspend fun uploadPhoto(fileName: String, fileUri: String) = dataSource.uploadPhoto(fileName, fileUri)

    suspend fun getPhotos() = dataSource.getPhotos()
}