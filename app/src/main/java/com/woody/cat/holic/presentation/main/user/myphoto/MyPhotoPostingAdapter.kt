package com.woody.cat.holic.presentation.main.user.myphoto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemMyPhotoPostingBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.user.myphoto.viewmodel.MyPhotoViewModel

class MyPhotoPostingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val myPhotoViewModel: MyPhotoViewModel
) : PagingDataAdapter<PostingItem, BaseViewHolder<PostingItem, MyPhotoViewModel>>(object : DiffUtil.ItemCallback<PostingItem>() {
    override fun areItemsTheSame(oldItem: PostingItem, newItem: PostingItem) = oldItem.postingId == newItem.postingId
    override fun areContentsTheSame(oldItem: PostingItem, newItem: PostingItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PostingItem, MyPhotoViewModel> {
        val binding = DataBindingUtil.inflate<ItemMyPhotoPostingBinding>(LayoutInflater.from(parent.context), R.layout.item_my_photo_posting, parent, false)

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<PostingItem, MyPhotoViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item, myPhotoViewModel)
        }
    }
}