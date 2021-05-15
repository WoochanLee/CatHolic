package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import java.io.File

interface PhotoRepository {

    companion object {
        const val MAX_USER_PROFILE_PHOTO_IMAGE_SIZE = 1024
        const val MAX_USER_BACKGROUND_PHOTO_IMAGE_SIZE = 1024
        const val MAX_CAT_PHOTO_IMAGE_SIZE = 1024
    }

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