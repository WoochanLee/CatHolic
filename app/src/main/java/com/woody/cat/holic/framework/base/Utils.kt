package com.woody.cat.holic.framework.base

import android.content.Context
import android.graphics.Color
import com.woody.cat.holic.R
import com.yanzhenjie.album.api.widget.Widget
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

fun Context.makeCustomAlbumWidget(): Widget {
    return Widget.newDarkBuilder(this)
        .title(getString(R.string.select_cats))
        .statusBarColor(Color.BLACK)
        .toolBarColor(Color.BLACK)
        .navigationBarColor(Color.BLACK)
        .build()
}