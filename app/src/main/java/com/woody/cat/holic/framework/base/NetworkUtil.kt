package com.woody.cat.holic.framework.base

import com.woody.cat.holic.data.common.Resource

inline fun <T> handleResourceResult(
    resource: Resource<T>,
    onSuccess: (T) -> Unit = {},
    onError: (Exception) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    when (resource) {
        is Resource.Success -> {
            onSuccess(resource.data)
            onComplete()
        }
        is Resource.Error -> {
            resource.exception.printStackTraceIfDebug()
            onError(resource.exception)
            onComplete()
        }
    }
}