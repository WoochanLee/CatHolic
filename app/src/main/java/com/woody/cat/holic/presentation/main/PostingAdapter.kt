package com.woody.cat.holic.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemMainPostingBinding
import com.woody.cat.holic.framework.base.BaseViewHolder
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel

class PostingAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val activityViewModel: MainViewModel
) : RecyclerView.Adapter<BaseViewHolder<PostingItem, MainViewModel>>() {

    private var data = listOf<PostingItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PostingItem, MainViewModel> {
        val binding = DataBindingUtil.inflate<ItemMainPostingBinding>(LayoutInflater.from(parent.context), R.layout.item_main_posting, parent, false).apply {
            viewModel = activityViewModel
        }

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<PostingItem, MainViewModel>, position: Int) {
        holder.bind(position, data[position], activityViewModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(data: List<PostingItem>) {
        this.data = data
        notifyDataSetChanged()
    }
}