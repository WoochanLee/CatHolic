package com.woody.cat.holic.usecase

import com.woody.cat.holic.data.PhotoRepository
import java.io.File

class UploadPhoto(private val photoRepository: PhotoRepository) {
    //TODO: file size limit
    suspend operator fun invoke(file: File, onProgress: (Int) -> Unit) = photoRepository.uploadPhoto(file, onProgress)
}