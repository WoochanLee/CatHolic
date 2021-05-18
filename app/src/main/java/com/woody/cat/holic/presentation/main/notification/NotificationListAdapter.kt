package com.woody.cat.holic.presentation.main.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemNotificationListBinding
import com.woody.cat.holic.domain.Notification
import com.woody.cat.holic.framework.base.BaseViewHolder

class NotificationListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val notificationListViewModel: NotificationListViewModel
) : PagingDataAdapter<Notification, BaseViewHolder<Notification, NotificationListViewModel>>(object : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Notification, newItem: Notification) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Notification, NotificationListViewModel> {
        val binding = DataBindingUtil.inflate<ItemNotificationListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_notification_list,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Notification, NotificationListViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item)
            (holder.binding as ItemNotificationListBinding).onClickNotification = notificationListViewModel::onClickNotification
        }
    }
}