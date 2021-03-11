package com.woody.cat.holic.framework

import com.woody.cat.holic.interactors.GetPhotos
import com.woody.cat.holic.interactors.UploadPhoto

data class Interactors(
    val getPhotos: GetPhotos,
    val uploadPhoto: UploadPhoto
)