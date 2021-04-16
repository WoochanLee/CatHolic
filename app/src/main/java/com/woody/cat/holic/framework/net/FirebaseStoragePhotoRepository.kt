package com.woody.cat.holic.framework.net

import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.STORAGE_CAT_PHOTO_PATH
import com.woody.cat.holic.framework.STORAGE_USER_BACKGROUND_PHOTO_PATH
import com.woody.cat.holic.framework.STORAGE_USER_PROFILE_PHOTO_PATH
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.framework.base.getFileExtension
import com.woody.cat.holic.framework.base.getJpegByteArray
import kotlinx.coroutines.CompletableDeferred
import java.io.File

class FirebaseStoragePhotoRepository(private val storageRef: StorageReference) : PhotoRepository {

    companion object {
        const val MAX_RETRY_TIME_MILLIS = 1000L
    }

    override suspend fun uploadCatPhoto(
        userId: String,
        file: File,
        onProgress: (Int) -> Unit
    ): Resource<Photo> {
        val dataDeferred = CompletableDeferred<Resource<Photo>>()

        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_CAT_PHOTO_IMAGE_SIZE)

        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_CAT_PHOTO_PATH, userId))
        val task = catsRef.putBytes(imageByteArray)
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

    override suspend fun uploadUserProfilePhoto(userId: String, file: File): Resource<Photo> {
        val dataDeferred = CompletableDeferred<Resource<Photo>>()

        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_USER_PROFILE_PHOTO_IMAGE_SIZE)

        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_USER_PROFILE_PHOTO_PATH, userId))
        val task = catsRef.putBytes(imageByteArray)
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

    override suspend fun uploadUserBackgroundPhoto(userId: String, file: File): Resource<Photo> {
        val dataDeferred = CompletableDeferred<Resource<Photo>>()

        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_USER_BACKGROUND_PHOTO_IMAGE_SIZE)

        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_USER_BACKGROUND_PHOTO_PATH, userId))
        val task = catsRef.putBytes(imageByteArray)
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

    private fun makeFirebaseStorageUploadFilePath(file: File, path: String, userId: String): String {
        return "$path/${userId}/${(0..Long.MAX_VALUE).random()}.${getFileExtension(file.name)}"
    }
}