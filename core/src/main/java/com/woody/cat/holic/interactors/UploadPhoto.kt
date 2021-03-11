package com.woody.cat.holic.interactors

import com.woody.cat.holic.data.PhotoRepository

class UploadPhoto(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(fileName: String, fileUri: String) = photoRepository.uploadPhoto(fileName, fileUri)
}