package com.woody.cat.holic.framework.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.BR

class BaseViewHolder<M : Any, VM : Any>(
    private val binding: ViewDataBinding,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, model: M, viewModel: VM) {
        binding.setVariable(BR.position, position)
        binding.setVariable(BR.model, model)
        binding.setVariable(BR.viewModel, viewModel)
        binding.lifecycleOwner = lifecycleOwner
        binding.executePendingBindings()
    }
}