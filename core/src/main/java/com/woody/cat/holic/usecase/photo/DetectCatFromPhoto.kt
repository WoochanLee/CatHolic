package com.woody.cat.holic.usecase.photo

import com.woody.cat.holic.data.PhotoAnalyzer

class DetectCatFromPhoto(private val photoAnalyzer: PhotoAnalyzer) {
    suspend operator fun invoke(uri: String) = photoAnalyzer.detectCatFromLocalPhoto(uri)
}