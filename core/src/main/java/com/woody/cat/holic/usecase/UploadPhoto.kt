package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PhotoRepository

class UploadPhoto(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(fileUri: String, onProgress: (Int) -> Unit) = photoRepository.uploadPhoto(fileUri, onProgress)
}