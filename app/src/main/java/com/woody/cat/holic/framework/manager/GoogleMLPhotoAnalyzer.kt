package com.woody.cat.holic.framework.manager

import android.content.Context
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.woody.cat.holic.data.PhotoAnalyzer
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.base.CatHolicLogger
import kotlinx.coroutines.tasks.await
import java.io.File

class GoogleMLPhotoAnalyzer(private val context: Context) : PhotoAnalyzer {

    companion object {
        const val DETECT_CAT_TEXT = "Cat"
        const val DETECT_CAT_CONFIDENCE = 0.50
    }

    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun detectCatFromLocalPhoto(uri: String): Resource<Boolean> {
        val image = InputImage.fromFilePath(context, File(uri).toUri())

        return try {
            val labels = imageLabeler.process(image).await()

            for (label in labels) {
                CatHolicLogger.log("detect : ${label.text} , confidence : ${(label.confidence * 100).toInt()}%")
                if (label.text == DETECT_CAT_TEXT/* && label.confidence > DETECT_CAT_CONFIDENCE*/) {
                    return Resource.Success(true)
                }
            }
            Resource.Success(false)
        }catch (e: Exception) {
            Resource.Error(e)
        }
    }
}