package com.woody.cat.holic.framework.net

import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.getFileExtension
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class FirebaseStoragePhotoRepository(private val storageRef: StorageReference) : PhotoRepository {

    companion object {
        const val STORAGE_PATH = "cat"
        const val MAX_RETRY_TIME_MILLIS = 1000L
    }

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

    private fun makeFirebaseStorageUploadFilePath(file: File, userId: String): String {
        return "$STORAGE_PATH/${userId}/${(0..Long.MAX_VALUE).random()}.${getFileExtension(file.name)}"
    }
}