package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import java.io.File

interface PhotoRepository {

    suspend fun uploadCatPhoto(
        userId: String,
        file: File,
        onProgress: (Int) -> Unit
    ): Resource<Photo>

    suspend fun uploadUserProfilePhoto(
        userId: String,
        file: File
    ): Resource<Photo>

    suspend fun uploadUserBackgroundPhoto(
        userId: String,
        file: File
    ): Resource<Photo>
}