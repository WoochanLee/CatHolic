package com.woody.cat.holic.presentation.main.posting.likelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemLikeListBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.UserItem

class LikeListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val likeListViewModel: LikeListViewModel
) : PagingDataAdapter<UserItem, BaseViewHolder<UserItem, LikeListViewModel>>(object : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem.userId == newItem.userId
    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserItem, LikeListViewModel> {
        val binding = DataBindingUtil.inflate<ItemLikeListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_like_list,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UserItem, LikeListViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item, likeListViewModel)
        }
    }
}