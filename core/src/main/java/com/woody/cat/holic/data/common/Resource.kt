package com.woody.cat.holic.data.common

sealed class Resource<T>() {
    //class Loading<T>() : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val exception: Exception) : Resource<T>()
}