package com.woody.cat.holic.presentation.main.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.woody.cat.holic.BR
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemAdmobBinding
import com.woody.cat.holic.databinding.ItemGalleryPostingBinding
import com.woody.cat.holic.framework.paging.item.AdItem
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.framework.paging.item.RecyclerViewItem

class PostingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val postingViewModel: PostingViewModel
) : PagingDataAdapter<RecyclerViewItem, PostingAdapter.PostingImageViewPagerViewHolder>(object :
    DiffUtil.ItemCallback<RecyclerViewItem>() {
    override fun areItemsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem) = oldItem.postingId == newItem.postingId
    override fun areContentsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem) = oldItem.postingId == newItem.postingId
}) {

    private var viewPagerPositionMap = mutableMapOf<String, Int>()

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is AdItem) {
            PostingViewType.AD_VIEW_TYPE.number
        } else {
            PostingViewType.NORMAL_VIEW_TYPE.number
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostingImageViewPagerViewHolder {
        var postingItemAdapter: PostingItemAdapter? = null
        val binding = if (viewType == PostingViewType.NORMAL_VIEW_TYPE.number) {
            DataBindingUtil.inflate<ItemGalleryPostingBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_gallery_posting,
                parent,
                false
            ).apply {
                postingItemAdapter = PostingItemAdapter(
                    this@PostingAdapter.lifecycleOwner,
                    pivGalleryPosting,
                    ImageView.ScaleType.CENTER_CROP,
                    postingViewModel::onClickPostingImage
                ).also { postingItemViewPagerAdapter ->
                    vpImage.adapter = postingItemViewPagerAdapter
                    vpImage.registerOnPageChangeCallback(postingItemViewPagerAdapter.pageChangeListener)
                }
            }
        } else {
            DataBindingUtil.inflate<ItemAdmobBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_admob,
                parent,
                false
            ).apply {
                avAdmob.loadAd(AdRequest.Builder().build())
            }
        }

        return PostingImageViewPagerViewHolder(binding, lifecycleOwner, postingItemAdapter)
    }

    override fun onViewRecycled(holder: PostingImageViewPagerViewHolder) {

        if (holder.getCurrentViewType() == PostingViewType.NORMAL_VIEW_TYPE) {
            viewPagerPositionMap[(holder.model as PostingItem).postingId] = holder.getCurrentViewPagerItemPosition()
        }

        super.onViewRecycled(holder);
    }

    override fun onBindViewHolder(holder: PostingImageViewPagerViewHolder, position: Int) {
        getItem(position)?.let { item ->
            if (item is PostingItem) {
                holder.bind(position, item, postingViewModel, item)

                holder.setViewPagerItemPosition(viewPagerPositionMap[item.postingId] ?: 0)
            }
        }
    }

    class PostingImageViewPagerViewHolder(
        val binding: ViewDataBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val postingItemAdapter: PostingItemAdapter?
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var model: RecyclerViewItem

        fun bind(position: Int, model: RecyclerViewItem, viewModel: PostingViewModel, postingItem: PostingItem) {
            this.model = model
            postingItemAdapter?.refreshItem(postingItem)
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.model, model)
            binding.setVariable(BR.viewModel, viewModel)
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }

        fun getCurrentViewType() = if (binding is ItemGalleryPostingBinding) PostingViewType.NORMAL_VIEW_TYPE else PostingViewType.AD_VIEW_TYPE

        fun getCurrentViewPagerItemPosition(): Int {
            return if (getCurrentViewType() == PostingViewType.NORMAL_VIEW_TYPE) {
                (binding as ItemGalleryPostingBinding).vpImage.currentItem
            } else {
                0
            }
        }

        fun setViewPagerItemPosition(position: Int) {
            (binding as ItemGalleryPostingBinding).vpImage.apply {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        setCurrentItem(position, false)
                    }
                })
            }
        }
    }

    enum class PostingViewType(val number: Int) {
        NORMAL_VIEW_TYPE(0),
        AD_VIEW_TYPE(1)
    }
}