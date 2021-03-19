package com.woody.cat.holic.framework

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.getFileExtension
import com.woody.cat.holic.framework.net.common.NotSignedInException
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class PhotoRepositoryImpl(private val firebaseUserManager: FirebaseUserManager) : PhotoRepository {

    companion object {
        const val STORAGE_PATH = "cat"
        const val MAX_RETRY_TIME_MILLIS = 1000L
    }

    private val storageRef = FirebaseStorage.getInstance().apply {
        maxDownloadRetryTimeMillis = MAX_RETRY_TIME_MILLIS
        maxUploadRetryTimeMillis = MAX_RETRY_TIME_MILLIS
    }.reference

    override suspend fun uploadPhoto(
        file: File,
        onProgress: (Int) -> Unit
    ): Resource<Photo> {
        val dataDeferred = CompletableDeferred<Resource<Photo>>()

        val user = firebaseUserManager.getCurrentUser() ?: return Resource.Error(NotSignedInException())

        val catsRef = storageRef.child(makeUploadFilePath(file, user.userId))
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
                    dataDeferred.complete(Resource.Success(Photo(user, it.toString())))
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

    private fun makeUploadFilePath(file: File, userId: String): String {
        return "${STORAGE_PATH}/${userId}/${(0..Long.MAX_VALUE).random()}.${getFileExtension(file.name)}"
    }

    /*override suspend fun getPhotos(): List<Resource<Photo>> {
        val dataDeferred = CompletableDeferred<List<Resource<Photo>>>()
        storageRef.child("cat").listAll()
            .addOnSuccessListener {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        dataDeferred.complete(getPhotoUrls(it))
                    }
                }
            }

        return dataDeferred.await()
    }

    private suspend fun getPhotoUrls(result: ListResult): List<Resource<Photo>> {
        return result.items.map {
            it.downloadUrl
        }.map {
            CompletableDeferred<Resource<Photo>>().apply {
                it.addOnSuccessListener { uri ->
                    this.complete(Resource.Success(Photo(uri.toString())))
                }.addOnFailureListener {
                    this.complete(Resource.Error(it))
                }
            }
        }.map {
            it.await()
        }
    }*/
}