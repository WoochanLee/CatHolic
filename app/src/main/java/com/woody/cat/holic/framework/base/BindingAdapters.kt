package com.woody.cat.holic.framework.base

import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.woody.cat.holic.R
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.user.myphoto.MyPhotoViewModel

@BindingAdapter("imageUrl")
fun ImageView.bindImage(imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.congshu_cat)
            .error(R.drawable.woody_cat)
            .into(this)
    }
}

@BindingAdapter("tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}

@BindingAdapter("postingItem", "myPhotoItemMenuListener")
fun ImageButton.setMyPhotoItemMenuListener(postingItem: PostingItem, myPhotoItemMenuListener: MyPhotoViewModel.MyPhotoItemMenuListener) {
    setOnClickListener {
        val popup = PopupMenu(context, this)
        popup.inflate(R.menu.posting_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_delete -> {
                    myPhotoItemMenuListener.onClickDelete(postingItem.postingId)
                    return@setOnMenuItemClickListener true
                }

                R.id.menu_download -> {
                    myPhotoItemMenuListener.onClickDownload(postingItem.downloadUrl)
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }
}