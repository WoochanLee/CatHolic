package com.woody.cat.holic.framework.base

import com.woody.cat.holic.data.common.Resource

fun <T> handleNetworkResult(
    resource: Resource<T>,
    onSuccess: (T) -> Unit,
    onError: (Exception) -> Unit
) {
    when (resource) {
        is Resource.Success -> onSuccess(resource.data)
        is Resource.Error -> onError(resource.exception)
    }
}