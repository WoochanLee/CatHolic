package com.woody.cat.holic.presentation.main.user.profile.follower

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemUserListBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.UserItem

class FollowerListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val followerListViewModel: FollowerListViewModel
) : PagingDataAdapter<UserItem, BaseViewHolder<UserItem, FollowerListViewModel>>(object : DiffUtil.ItemCallback<UserItem>() {
    override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem.userId == newItem.userId
    override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UserItem, FollowerListViewModel> {
        val binding = DataBindingUtil.inflate<ItemUserListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_user_list,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UserItem, FollowerListViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item)
            (holder.binding as ItemUserListBinding).onClickProfile = followerListViewModel::onClickProfile
        }
    }
}