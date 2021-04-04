package com.woody.cat.holic.usecase.photo

import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.domain.User
import java.io.File

class UploadPhoto(private val photoRepository: PhotoRepository) {
    //TODO: file size limit
    suspend operator fun invoke(userId: String, file: File, onProgress: (Int) -> Unit) = photoRepository.uploadPhoto(userId, file, onProgress)
}