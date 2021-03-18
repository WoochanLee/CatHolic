package com.woody.cat.holic.framework.base


fun getFileExtension(filePath: String): String {
    var extension = "";

    val index = filePath.lastIndexOf('.');
    if (index > 0) {
        extension = filePath.substring(index + 1);
    }
    return extension
}