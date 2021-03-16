package com.woody.cat.holic.presentation.upload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemUploadUploadedImageBigBinding
import com.woody.cat.holic.framework.base.BaseViewHolder

class UploadBigPreviewAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: UploadViewModel
) : RecyclerView.Adapter<BaseViewHolder<String, UploadViewModel>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String, UploadViewModel> {

        val binding =
            DataBindingUtil.inflate<ItemUploadUploadedImageBigBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_upload_uploaded_image_big,
                parent,
                false
            )

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String, UploadViewModel>, position: Int) {
        holder.bind(position, viewModel.previewData[position], viewModel)
    }

    override fun getItemCount(): Int {
        return viewModel.previewData.size
    }

    fun checkAndChangeArrowButtonStatus(currentPosition: Int) {
        viewModel.checkAndChangeArrowButtonStatus(currentPosition, viewModel.previewData.size)
    }
}