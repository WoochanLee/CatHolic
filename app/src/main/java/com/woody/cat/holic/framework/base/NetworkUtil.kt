package com.woody.cat.holic.framework.base

import com.woody.cat.holic.data.common.Resource

inline fun <T> handleResourceResult(
    resource: Resource<T>,
    onSuccess: (T) -> Unit,
    noinline onError: ((Exception) -> Unit)? = null
) {
    when (resource) {
        is Resource.Success -> onSuccess(resource.data)
        is Resource.Error -> onError?.invoke(resource.exception)
    }
}