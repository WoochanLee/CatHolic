package com.woody.cat.holic.framework.base

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
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

fun Context.makeCustomAlbumWidget(@StringRes title: Int): Widget {
    return Widget.newDarkBuilder(this)
        .title(getString(title))
        .statusBarColor(Color.BLACK)
        .toolBarColor(Color.BLACK)
        .navigationBarColor(Color.BLACK)
        .build()
}

fun hideKeyboard(context: Context, focusView: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(focusView.windowToken, 0)
}

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}