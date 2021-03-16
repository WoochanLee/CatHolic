package com.woody.cat.holic.presentation.main.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemMainPhotoBinding
import com.woody.cat.holic.domain.Photo
import com.woody.cat.holic.framework.base.BaseViewHolder

class PhotoAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: GalleryViewModel
) : RecyclerView.Adapter<BaseViewHolder<Photo, GalleryViewModel>>() {

    private var data = listOf<Photo>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Photo, GalleryViewModel> {
        val binding = DataBindingUtil.inflate<ItemMainPhotoBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_main_photo,
            parent,
            false
        )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Photo, GalleryViewModel>, position: Int) {
        holder.bind(position, data[position], viewModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun refreshData(data: List<Photo>) {
        this.data = data
        notifyDataSetChanged()
    }
}