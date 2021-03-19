package com.woody.cat.holic.presentation.main.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemMainPostingBinding
import com.woody.cat.holic.domain.Posting
import com.woody.cat.holic.framework.base.BaseViewHolder

class GalleryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: GalleryViewModel
) : RecyclerView.Adapter<BaseViewHolder<Posting, GalleryViewModel>>() {

    private var data = listOf<Posting>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Posting, GalleryViewModel> {
        val binding = DataBindingUtil.inflate<ItemMainPostingBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_main_posting,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Posting, GalleryViewModel>, position: Int) {
        holder.bind(position, data[position], viewModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(data: List<Posting>) {
        this.data = data
        notifyDataSetChanged()
    }
}