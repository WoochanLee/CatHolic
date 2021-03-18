package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import java.io.File

interface PhotoRepository {

    suspend fun uploadPhoto(file: File, onProgress: (Int) -> Unit): Resource<Photo>

    //suspend fun getPhotos(): List<Resource<Photo>>
}