package com.woody.cat.holic.interactors

import com.woody.cat.holic.data.PhotoRepository

class GetPhotos(private val photoRepository: PhotoRepository) {
    suspend operator fun invoke() = photoRepository.getPhotos()
}