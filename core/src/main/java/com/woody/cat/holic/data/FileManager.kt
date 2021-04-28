package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface FileManager {

    suspend fun downloadPhoto(imageUrl: String): Resource<Unit>
}