package com.woody.cat.holic.framework

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.woody.cat.holic.data.PhotoDataSource
import com.woody.cat.holic.domain.Photo
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseStoragePhotoDataSource : PhotoDataSource {

    private val storageRef = FirebaseStorage.getInstance().reference

    // https://stackoverflow.com/a/48562175/9797457
    override suspend fun uploadPhoto(fileName: String, fileUri: String) {
        val dataDeferred = CompletableDeferred<Unit>()

        val catsRef = storageRef.child("cat/${fileName}")
        catsRef.putFile(Uri.parse(fileUri))
            .addOnSuccessListener {
                println("success to upload")
                dataDeferred.complete(Unit)
            }
            .addOnFailureListener {
                println("fail to upload")
                dataDeferred.complete(Unit)
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