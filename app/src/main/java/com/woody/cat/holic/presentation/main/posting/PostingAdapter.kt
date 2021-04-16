package com.woody.cat.holic.presentation.main.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemMainPostingBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.PostingItem

class PostingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val postingViewModel: PostingViewModel
) : PagingDataAdapter<PostingItem, BaseViewHolder<PostingItem, PostingViewModel>>(object : DiffUtil.ItemCallback<PostingItem>() {
    override fun areItemsTheSame(oldItem: PostingItem, newItem: PostingItem) = oldItem.postingId == newItem.postingId
    override fun areContentsTheSame(oldItem: PostingItem, newItem: PostingItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PostingItem, PostingViewModel> {
        val binding = DataBindingUtil.inflate<ItemMainPostingBinding>(LayoutInflater.from(parent.context), R.layout.item_main_posting, parent, false)

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<PostingItem, PostingViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item, postingViewModel)
        }
    }
}