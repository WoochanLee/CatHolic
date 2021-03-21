package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.domain.User
import java.io.File

interface PhotoRepository {

    suspend fun uploadPhoto(user: User, file: File, onProgress: (Int) -> Unit): Resource<Photo>
}