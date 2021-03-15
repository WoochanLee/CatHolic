package com.woody.cat.holic.framework

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.CatHolicLogger
import kotlinx.coroutines.*

class PhotoRepositoryImpl : PhotoRepository {

    companion object {
        const val STORAGE_PATH = "cat"
    }

    private val storageRef = FirebaseStorage.getInstance().reference

    // https://stackoverflow.com/a/48562175/9797457
    override suspend fun uploadPhoto(fileUri: String, onProgress: (Int) -> Unit): Photo {
        val dataDeferred = CompletableDeferred<Photo>()

        val catsRef = storageRef.child("${STORAGE_PATH}/${(0..Int.MAX_VALUE).random()}")
        catsRef.putFile(Uri.parse(fileUri))
            .addOnProgressListener { snapshot ->
                val progress: Int = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toInt()
                onProgress(progress)
                println("upload progress : $progress")
            }
            .addOnSuccessListener {
                CatHolicLogger.log("success to upload")
                catsRef.downloadUrl.addOnSuccessListener {
                    dataDeferred.complete(Photo(it.toString()))
                }.addOnFailureListener {
                    CatHolicLogger.log("fail to get download url")
                    throw it
                }
            }
            .addOnFailureListener {
                CatHolicLogger.log("fail to upload")
                throw it
            }

        return dataDeferred.await()
    }

    override suspend fun getPhotos(): List<Photo> {
        val dataDeferred = CompletableDeferred<List<Photo>>()
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

    private suspend fun getPhotoUrls(result: ListResult): List<Photo> {
        return result.items.map {
            it.downloadUrl
        }.map {
            CompletableDeferred<Photo>().apply {
                it.addOnSuccessListener { uri ->
                    this.complete(Photo(uri.toString()))
                }.addOnFailureListener {
                    this.complete(Photo(null))
                }
            }
        }.map {
            it.await()
        }
    }
//    override suspend fun getPhotos(): List<String> {
//        val photoList = mutableListOf<String>()
//
//        val dataDeferred = CompletableDeferred<List<String>>()
//        storageRef.child("cat").listAll()
//            .addOnSuccessListener {
//                it.items.forEach { storageReference ->
//                    storageReference.downloadUrl.addOnSuccessListener { uri ->
//                        photoList.add(uri.toString())
//                        if (photoList.size == it.items.size) {
//                            dataDeferred.complete(photoList)
//                        }
//                    }
//                }
//            }
//
//        return dataDeferred.await()
//    }

    /*override suspend fun getPhotos(): List<String> {
        val photoList = mutableListOf<String>()

        val dataDeferred = CompletableDeferred<List<String>>()
        storageRef.child("cat").listAll()
            .addOnSuccessListener {
                it.items.forEach { storageReference ->
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            val def = CompletableDeferred<String>()
                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                def.complete(uri.toString())
                            }
                            photoList.add(def.await())
                        }
                    }
                }
                dataDeferred.complete(photoList)
            }

        return dataDeferred.await()
    }*/

}