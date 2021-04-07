package com.woody.cat.holic.framework.base

import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.woody.cat.holic.R

@BindingAdapter("imageUrl")
fun ImageView.bindImage(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(context)
            .load(imageUrl)
            .error(ContextCompat.getDrawable(context, R.drawable.congshu_cat))
            .into(this)
    }
}

@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}

@BindingAdapter("onClickDelete")
fun ImageButton.setOnClickDeleteMenu(onClickDelete: () -> Unit) {
    setOnClickListener {
        val popup = PopupMenu(context, this)
        popup.inflate(R.menu.posting_menu)
        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_delete -> {
                    onClickDelete()
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }
}