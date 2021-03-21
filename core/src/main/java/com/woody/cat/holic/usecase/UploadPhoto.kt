package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.domain.User
import java.io.File

class UploadPhoto(private val photoRepository: PhotoRepository) {
    //TODO: file size limit
    suspend operator fun invoke(user: User, file: File, onProgress: (Int) -> Unit) = photoRepository.uploadPhoto(user, file, onProgress)
}