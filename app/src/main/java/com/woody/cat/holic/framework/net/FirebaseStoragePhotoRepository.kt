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
import kotlinx.coroutines.tasks.await
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
        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_CAT_PHOTO_IMAGE_SIZE)
        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_CAT_PHOTO_PATH, userId))

        return try {
            catsRef.putBytes(imageByteArray)
                .addOnProgressListener { snapshot ->
                    val progress = (100f * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()
                    onProgress(progress)
                    CatHolicLogger.log("upload progress : $progress")
                }.await()

            return try {
                val url = catsRef.downloadUrl.await()
                Resource.Success(Photo(userId, url.toString()))
            } catch (e: Exception) {
                Resource.Error(e)
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun uploadUserProfilePhoto(userId: String, file: File): Resource<Photo> {
        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_USER_PROFILE_PHOTO_IMAGE_SIZE)
        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_USER_PROFILE_PHOTO_PATH, userId))

        return try {
            catsRef.putBytes(imageByteArray).await()

            return try {
                val url = catsRef.downloadUrl.await()

                Resource.Success(Photo(userId, url.toString()))
            } catch (e: Exception) {
                Resource.Error(e)
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun uploadUserBackgroundPhoto(userId: String, file: File): Resource<Photo> {
        val imageByteArray = getJpegByteArray(file.path, PhotoRepository.MAX_USER_BACKGROUND_PHOTO_IMAGE_SIZE)
        val catsRef = storageRef.child(makeFirebaseStorageUploadFilePath(file, STORAGE_USER_BACKGROUND_PHOTO_PATH, userId))

        return try {
            catsRef.putBytes(imageByteArray).await()

            return try {
                val url = catsRef.downloadUrl.await()

                Resource.Success(Photo(userId, url.toString()))
            } catch (e: Exception) {
                Resource.Error(e)
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun makeFirebaseStorageUploadFilePath(file: File, path: String, userId: String): String {
        return "$path/${userId}/${(0..Long.MAX_VALUE).random()}.${getFileExtension(file.name)}"
    }
}