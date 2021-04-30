package com.woody.cat.holic.presentation.main.posting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.ads.AdRequest
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemAdmobBinding
import com.woody.cat.holic.databinding.ItemMainPostingBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.AdItem
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.framework.paging.item.RecyclerViewItem

class PostingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val postingViewModel: PostingViewModel
) : PagingDataAdapter<RecyclerViewItem, BaseViewHolder<RecyclerViewItem, PostingViewModel>>(object : DiffUtil.ItemCallback<RecyclerViewItem>() {
    override fun areItemsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem) = oldItem.postingId == newItem.postingId
    override fun areContentsTheSame(oldItem: RecyclerViewItem, newItem: RecyclerViewItem) = oldItem.postingId == newItem.postingId
}) {

    companion object {
        const val NORMAL_VIEW_TYPE = 0
        const val AD_VIEW_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is AdItem) {
            AD_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RecyclerViewItem, PostingViewModel> {
        val binding = if (viewType == NORMAL_VIEW_TYPE) {
            DataBindingUtil.inflate<ItemMainPostingBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_main_posting,
                parent,
                false
            )
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

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RecyclerViewItem, PostingViewModel>, position: Int) {
        getItem(position)?.let { item ->
            if(item is PostingItem) {
                holder.bind(position, item, postingViewModel)
            }
        }
    }
}