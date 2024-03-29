package com.woody.cat.holic.presentation.upload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ItemUploadUploadImageBinding
import com.woody.cat.holic.databinding.ItemUploadUploadedImageSmallBinding
import com.woody.cat.holic.framework.base.BaseViewHolder

class UploadSmallPreviewAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: UploadViewModel
) : RecyclerView.Adapter<BaseViewHolder<UploadItem, UploadViewModel>>() {

    companion object {
        const val VIEW_TYPE_UPLOAD = 0
        const val VIEW_TYPE_IMAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<UploadItem, UploadViewModel> {

        val binding = when (viewType) {
            VIEW_TYPE_UPLOAD -> {
                DataBindingUtil.inflate<ItemUploadUploadImageBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_upload_upload_image,
                    parent,
                    false
                ).apply {
                    viewModel = this@UploadSmallPreviewAdapter.viewModel
                }
            }
            else -> {
                DataBindingUtil.inflate<ItemUploadUploadedImageSmallBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_upload_uploaded_image_small,
                    parent,
                    false
                )
            }
        }

        return BaseViewHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UploadItem, UploadViewModel>, position: Int) {
        if (position == 0) {
            return
        }

        viewModel.previewData.value?.get(position - 1)?.let { uploadingPhotoItem ->
            holder.bind(position - 1, uploadingPhotoItem, viewModel)
        }
    }

    override fun getItemCount(): Int {
        return (viewModel.previewData.value?.size ?: 0) + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_UPLOAD
        } else {
            VIEW_TYPE_IMAGE
        }
    }
}