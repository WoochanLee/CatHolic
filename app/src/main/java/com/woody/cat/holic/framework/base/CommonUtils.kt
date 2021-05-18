package com.woody.cat.holic.framework.base

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import com.woody.cat.holic.R
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

fun makeCommentDateString(time: Long): String {
    return SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.getDefault()).format(Date(time))
}

fun hideKeyboard(context: Context, focusView: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(focusView.windowToken, 0)
}

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.shareDynamicLink(@StringRes stringRes: Int, dynamicLink: String) {
    val intent = Intent(Intent.ACTION_SEND)
    val shareBody = getString(stringRes, dynamicLink)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, shareBody)
    startActivity(Intent.createChooser(intent, getString(R.string.choose_an_app_to_share)))
}