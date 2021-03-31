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
    return SimpleDateFormat("MM.dd, yyyy", Locale.getDefault()).format(this ?: return "")
}