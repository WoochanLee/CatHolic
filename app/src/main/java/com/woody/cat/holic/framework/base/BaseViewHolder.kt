package com.woody.cat.holic.framework.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.woody.cat.holic.BR

class BaseViewHolder<M : Any>(
    private val binding: ViewDataBinding,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.ViewHolder(binding.root) {
    lateinit var model: M

    fun bind(model: M) {
        this.model = model
        binding.setVariable(BR.model, this.model)
        binding.lifecycleOwner = lifecycleOwner
        binding.executePendingBindings()
    }
}