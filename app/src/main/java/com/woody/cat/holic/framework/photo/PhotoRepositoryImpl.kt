package com.woody.cat.holic.framework.photo

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.getFileExtension
import com.woody.cat.holic.framework.base.printStackTraceIfDebug
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class PhotoRepositoryImpl(private val context: Context, private val storageRef: StorageReference) : PhotoRepository {

    companion object {
        const val STORAGE_PATH = "cat"
        const val DETECT_CAT_TEXT = "Cat"
        const val DETECT_CAT_CONFIDENCE = 0.95
        const val MAX_RETRY_TIME_MILLIS = 1000L
    }

    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    override suspend fun uploadPhoto(
        userId: String,
        file: File,
        onProgress: (Int) -> Unit
    ): Resource<Photo> {
        val dataDeferred = CompletableDeferred<Resource<Photo>>()

        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, userId))
        val task = catsRef.putFile(Uri.fromFile(file))
            .addOnProgressListener { snapshot ->
                val progress = (100f * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()
                onProgress(progress)
                CatHolicLogger.log("upload progress : $progress")
            }
            .addOnSuccessListener {
                CatHolicLogger.log("success to upload")
                catsRef.downloadUrl.addOnSuccessListener {
                    CatHolicLogger.log("success to get download url")
                    dataDeferred.complete(Resource.Success(Photo(userId, it.toString())))
                }.addOnFailureListener {
                    CatHolicLogger.log("fail to get download url")
                    dataDeferred.complete(Resource.Error(it))
                }
            }
            .addOnFailureListener {
                CatHolicLogger.log("fail to upload")
                dataDeferred.complete(Resource.Error(it))
            }

        return try {
            dataDeferred.await()
        } catch (e: Exception) {
            task.cancel()
            Resource.Error(e)
        }
    }

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

    private fun makeFirebaseStorageUploadFilePath(file: File, userId: String): String {
        return "$STORAGE_PATH/${userId}/${(0..Long.MAX_VALUE).random()}.${getFileExtension(file.name)}"
    }
}