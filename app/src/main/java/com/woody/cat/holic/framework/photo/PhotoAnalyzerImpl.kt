package com.woody.cat.holic.framework.photo

import android.content.Context
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.woody.cat.holic.data.PhotoAnalyzer
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.printStackTraceIfDebug
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class PhotoAnalyzerImpl(private val context: Context) : PhotoAnalyzer {

    companion object {
        const val DETECT_CAT_TEXT = "Cat"
        const val DETECT_CAT_CONFIDENCE = 0.95
    }

    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun detectCatFromLocalPhoto(uri: String): Resource<Boolean> {
        val dataDeferred = CompletableDeferred<Resource<Boolean>>()

        val image = InputImage.fromFilePath(context, File(uri).toUri())

        imageLabeler.process(image)
            .addOnSuccessListener breaker@{ labels ->
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence

                    if (label.text == DETECT_CAT_TEXT && label.confidence > DETECT_CAT_CONFIDENCE) {
                        dataDeferred.complete(Resource.Success(true))
                        return@breaker
                    }
                    CatHolicLogger.log("detect : $text , confidence : ${(confidence * 100).toInt()}%")
                }
                dataDeferred.complete(Resource.Success(false))
            }
            .addOnFailureListener { e ->
                e.printStackTraceIfDebug()
                dataDeferred.complete(Resource.Error(e))
            }

        return dataDeferred.await()
    }
}