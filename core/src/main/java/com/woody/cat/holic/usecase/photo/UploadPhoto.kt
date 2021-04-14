package com.woody.cat.holic.usecase.photo

import com.woody.cat.holic.data.PhotoRepository
import java.io.File

class UploadPhoto(private val photoRepository: PhotoRepository) {
    //TODO: file size limit
    suspend fun uploadCatPhoto(userId: String, file: File, onProgress: (Int) -> Unit) = photoRepository.uploadCatPhoto(userId, file, onProgress)

    suspend fun uploadUserProfilePhoto(userId: String, file: File) = photoRepository.uploadUserProfilePhoto(userId, file)

    suspend fun uploadUserBackgroundPhoto(userId: String, file: File) = photoRepository.uploadUserBackgroundPhoto(userId, file)
}