package com.woody.cat.holic.framework.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.framework.Interactors

object BaseViewModelFactory : ViewModelProvider.Factory {

    lateinit var dependencies: Interactors

    fun inject(dependencies: Interactors) {
        BaseViewModelFactory.dependencies = dependencies
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (BaseViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass.getConstructor(Interactors::class.java).newInstance(dependencies)
        } else {
            throw IllegalStateException("Please extend BaseViewModel")
        }
    }
}