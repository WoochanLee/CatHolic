package com.woody.cat.holic.framework.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.IOException

fun getJpegByteArray(filePath: String, maxSize: Int): ByteArray {
    val orientation = getOrientationOfImage(filePath)
    val bitmap = BitmapFactory.decodeFile(filePath).run { getResizedBitmap(this, maxSize) }.run { getRotatedBitmap(this, orientation) }
    val byteStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteStream)
    return byteStream.toByteArray()
}

private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}

private fun getOrientationOfImage(filepath: String): Int {
    val exif = try {
        ExifInterface(filepath)
    } catch (e: IOException) {
        e.printStackTrace()
        return 0
    }
    val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
    if (orientation != -1) {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270
        }
    }
    return 0
}

private fun getRotatedBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    if (degrees == 0) return bitmap
    val m = Matrix()
    m.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
}