package com.woody.cat.holic.presentation.main.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemFollowListBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.UserItem

class FollowAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val followViewModel: FollowViewModel
) : PagingDataAdapter<UserItem, BaseViewHolder<UserItem, FollowViewModel>>(object : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem.userId == newItem.userId
    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserItem, FollowViewModel> {
        val binding = DataBindingUtil.inflate<ItemFollowListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_follow_list,
            parent,
            false
        ).apply {
            viewModel = followViewModel
        }

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UserItem, FollowViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item)
            (holder.binding as ItemFollowListBinding).onClickProfile = FollowViewModel::onClickProfile
        }
    }
}