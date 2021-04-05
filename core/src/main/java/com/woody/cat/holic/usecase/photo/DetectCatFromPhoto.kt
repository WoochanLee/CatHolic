package com.woody.cat.holic.usecase.photo

import com.woody.cat.holic.data.PhotoRepository

class DetectCatFromPhoto(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke(uri: String) = photoRepository.detectCatFromLocalPhoto(uri)
}