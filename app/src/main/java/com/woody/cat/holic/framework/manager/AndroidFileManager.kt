package com.woody.cat.holic.framework.manager

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.woody.cat.holic.data.FileManager
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.net.common.NetworkException
import kotlinx.coroutines.CompletableDeferred
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AndroidFileManager(private val context: Context) : FileManager {

    @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
    override suspend fun downloadPhoto(imageUrl: String): Resource<Unit> {

        try {
            val bitmap = getPhotoBitmap(imageUrl)
            val filename = "${(0..Long.MAX_VALUE).random()}.jpg"
            val mimeType = "image/jpeg"
            val directory = Environment.DIRECTORY_PICTURES
            val mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val imageOutStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, directory)
                }

                context.contentResolver.run {
                    val uri = insert(mediaContentUri, values) ?: return Resource.Error(IOException())
                    openOutputStream(uri) ?: return Resource.Error(IOException())
                }
            } else {
                val imagePath = Environment.getExternalStoragePublicDirectory(directory).absolutePath
                val image = File(imagePath, filename)
                FileOutputStream(image)
            }

            imageOutStream.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            return Resource.Success(Unit)
        } catch (e: Exception) {
            return Resource.Error(IOException())
        }
    }

    private suspend fun getPhotoBitmap(imageUrl: String): Bitmap {
        val resultDeferred = CompletableDeferred<Bitmap>()
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) = Unit
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    resultDeferred.complete(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    throw NetworkException()
                }
            })

        return resultDeferred.await()
    }
}