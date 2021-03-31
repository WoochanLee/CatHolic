package com.woody.cat.holic.framework.base

import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.woody.cat.holic.R

@BindingAdapter("imageUrl")
fun bindImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .error(ContextCompat.getDrawable(view.context, R.drawable.congshu_cat_white))
            .into(view)
    }
}

@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}