package com.woody.cat.holic.framework.base

import java.text.SimpleDateFormat
import java.util.*


fun getFileExtension(filePath: String): String {
    var extension = "";

    val index = filePath.lastIndexOf('.');
    if (index > 0) {
        extension = filePath.substring(index + 1);
    }
    return extension
}

fun Date?.makePostingDateString(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this ?: return "")
}

fun Date?.makeCommentDateString(): String {
    return SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.getDefault()).format(this ?: return "")
}