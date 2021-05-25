package com.woody.cat.holic.presentation.main.posting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView2
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemGalleryPostingImageBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.PostingItem

class PostingItemAdapter(
    private val lifecycleOwner: LifecycleOwner,
    pageIndicatorView: PageIndicatorView2,
    private val scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP,
    private val onClickPostingImage: (PostingItem) -> Unit = { },
    private val onLongClickPostingImage: () -> Unit = { },
) : RecyclerView.Adapter<BaseViewHolder<String, PostingViewModel>>() {

    var postingItem: PostingItem? = null

    val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            pageIndicatorView.selection = position
            postingItem?.let { item ->
                val imagePositionText = "${position + 1} / ${item.imageUrls.size}"
                item.imagePositionText.postValue(imagePositionText)
            }
        }
    }

    private val onClickPostingListener = View.OnClickListener {
        postingItem?.let { postingItem ->
            onClickPostingImage(postingItem)
        }
    }

    private val onLongClickPostingListener = View.OnLongClickListener {
        onLongClickPostingImage()
        true
    }

    fun refreshItem(item: PostingItem) {
        postingItem = item
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String, PostingViewModel> {
        val binding = DataBindingUtil.inflate<ItemGalleryPostingImageBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_gallery_posting_image,
            parent,
            false
        ).apply {
            ivItemGalleryPostingImage.scaleType = scaleType
            ivItemGalleryPostingImage.setOnClickListener(onClickPostingListener)
            ivItemGalleryPostingImage.setOnLongClickListener(onLongClickPostingListener)
        }
        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String, PostingViewModel>, position: Int) {
        holder.bind(position, postingItem?.imageUrls?.get(position) ?: "")
    }

    override fun getItemCount(): Int {
        return postingItem?.imageUrls?.size ?: 0
    }
}