package com.woody.cat.holic.usecase.photo

import com.woody.cat.holic.data.FileManager

class DownloadPhoto(private val fileManager: FileManager) {

    suspend operator fun invoke(imageUrl: String) = fileManager.downloadPhoto(imageUrl)
}