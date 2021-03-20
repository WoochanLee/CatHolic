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
import com.woody.cat.holic.presentation.main.MainViewModel

class GalleryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val activityViewModel: MainViewModel
) : RecyclerView.Adapter<BaseViewHolder<Posting, MainViewModel>>() {

    private var data = listOf<Posting>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Posting, MainViewModel> {
        val binding = DataBindingUtil.inflate<ItemMainPostingBinding>(LayoutInflater.from(parent.context), R.layout.item_main_posting, parent, false)

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Posting, MainViewModel>, position: Int) {
        holder.bind(position, data[position], activityViewModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(data: List<Posting>) {
        this.data = data
        notifyDataSetChanged()
    }
}