package com.woody.cat.holic.presentation.main.posting.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemPostingCommentBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.framework.paging.item.CommentItem

class CommentAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val commentViewModel: CommentViewModel
) : PagingDataAdapter<CommentItem, BaseViewHolder<CommentItem, CommentViewModel>>(object : DiffUtil.ItemCallback<CommentItem>() {
    override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem) = oldItem.commentId == newItem.commentId
    override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem) = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CommentItem, CommentViewModel> {
        val binding = DataBindingUtil.inflate<ItemPostingCommentBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_posting_comment,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<CommentItem, CommentViewModel>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(position, item, commentViewModel)
        }
    }
}